package io.metersphere.service;


import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.AtomicDouble;
import io.metersphere.base.domain.*;
import io.metersphere.base.mapper.ProjectMapper;
import io.metersphere.base.mapper.ext.ExtModuleNodeMapper;
import io.metersphere.commons.constants.TestCaseConstants;
import io.metersphere.commons.exception.MSException;
import io.metersphere.commons.utils.SessionUtils;
import io.metersphere.exception.ExcelException;
import io.metersphere.i18n.Translator;
import io.metersphere.log.utils.ReflexObjectUtil;
import io.metersphere.log.vo.DetailColumn;
import io.metersphere.log.vo.OperatingLogDetails;
import io.metersphere.log.vo.api.ModuleReference;
import io.metersphere.track.dto.EditModuleDateDTO;
import io.metersphere.track.dto.ModuleNodeDTO;
import io.metersphere.track.dto.TestCaseNodeDTO;
import io.metersphere.track.request.testcase.DragNodeRequest;
import io.metersphere.track.request.testcase.QueryNodeRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class BaseModuleService extends NodeTreeService<ModuleNodeDTO> {

    private String tableName;

    @Resource
    ExtModuleNodeMapper extModuleNodeMapper;
    @Resource
    SqlSessionFactory sqlSessionFactory;
    @Resource
    ProjectMapper projectMapper;


    public BaseModuleService() {
        super(ModuleNodeDTO.class);
    }

    public BaseModuleService(String tableName) {
        this();
        this.tableName = tableName;
    }

    public String addNode(ModuleNode module) {
        validateNode(module);
        module.setCreateTime(System.currentTimeMillis());
        module.setUpdateTime(System.currentTimeMillis());
        if (StringUtils.isBlank(module.getId())) {
            module.setId(UUID.randomUUID().toString());
        }
        module.setCreateUser(SessionUtils.getUserId());
        double pos = getNextLevelPos(module.getProjectId(), module.getLevel(), module.getParentId());
        module.setPos(pos);
        extModuleNodeMapper.insertSelective(tableName, module);
        return module.getId();
    }

    public List<String> getNodes(String nodeId) {
        return extModuleNodeMapper.getNodeIdsByPid(tableName, nodeId);
    }

    public ModuleNode get(String id) {
        return extModuleNodeMapper.selectByPrimaryKey(tableName, id);
    }

    private void validateNode(ModuleNode node) {
        if (node.getLevel() > TestCaseConstants.MAX_NODE_DEPTH) {
            throw new RuntimeException(Translator.get("test_case_node_level_tip")
                    + TestCaseConstants.MAX_NODE_DEPTH + Translator.get("test_case_node_level"));
        }
        checkTestCaseNodeExist(node);
    }

    private void checkTestCaseNodeExist(ModuleNode node) {
        if (node.getName() != null) {
            TestCaseNodeExample example = new TestCaseNodeExample();
            TestCaseNodeExample.Criteria criteria = example.createCriteria();
            criteria.andNameEqualTo(node.getName())
                    .andProjectIdEqualTo(node.getProjectId());

            if (StringUtils.isNotBlank(node.getParentId())) {
                criteria.andParentIdEqualTo(node.getParentId());
            } else {
                criteria.andLevelEqualTo(node.getLevel());
            }

            if (StringUtils.isNotBlank(node.getId())) {
                criteria.andIdNotEqualTo(node.getId());
            }
            if (extModuleNodeMapper.selectByExample(tableName, example).size() > 0) {
                MSException.throwException(Translator.get("test_case_module_already_exists"));
            }
        }
    }

    public ModuleNode getDefaultNode(String projectId, String defaultName) {
        TestCaseNodeExample example = new TestCaseNodeExample();
        example.createCriteria().andProjectIdEqualTo(projectId).andNameEqualTo(Optional.ofNullable(defaultName).orElse("???????????????")).andParentIdIsNull();
        List<ModuleNode> list = extModuleNodeMapper.selectByExample(tableName, example);
        if (CollectionUtils.isEmpty(list)) {
            ModuleNode record = new ModuleNode();
            record.setId(UUID.randomUUID().toString());
            record.setCreateUser(SessionUtils.getUserId());
            record.setName(Optional.ofNullable(defaultName).orElse("???????????????"));
            record.setPos(1.0);
            record.setLevel(1);
            record.setCreateTime(System.currentTimeMillis());
            record.setUpdateTime(System.currentTimeMillis());
            record.setProjectId(projectId);
            extModuleNodeMapper.insert(tableName, record);
            record.setCaseNum(0);
            return record;
        } else {
            return list.get(0);
        }
    }

    public List<ModuleNodeDTO> getNodeTreeByProjectIdWithCount(String projectId, Function<QueryNodeRequest, List<Map<String, Object>>> getModuleCountFunc, String defaultName) {
        // ?????????????????????????????????????????????????????????????????????
        this.getDefaultNode(projectId, defaultName);

        List<ModuleNodeDTO> moduleNodes = extModuleNodeMapper.getNodeTreeByProjectId(tableName, projectId);

        if (getModuleCountFunc != null) {
            buildNodeCount(projectId, moduleNodes, getModuleCountFunc);
        }

        return getNodeTrees(moduleNodes);
    }

    public List<ModuleNodeDTO> getNodeTreeByProjectId(String projectId, String defaultName) {
        return getNodeTreeByProjectIdWithCount(projectId, null, defaultName);
    }

    protected void buildNodeCount(String projectId, List<ModuleNodeDTO> moduleNodes, Function<QueryNodeRequest, List<Map<String, Object>>> getModuleCountFunc) {
        this.buildNodeCount(projectId, moduleNodes, getModuleCountFunc, null);
    }

    protected void buildNodeCount(String projectId, List<ModuleNodeDTO> moduleNodes, Function<QueryNodeRequest, List<Map<String, Object>>> getModuleCountFunc,
                                QueryNodeRequest request) {
        if (request == null) {
            request = new QueryNodeRequest();
        }
        request.setProjectId(projectId);

        //????????????for????????????SQL????????????????????????
        List<String> allModuleIdList = new ArrayList<>();
        for (ModuleNodeDTO node : moduleNodes) {
            List<String> moduleIds = new ArrayList<>();
            moduleIds = nodeList(moduleNodes, node.getId(), moduleIds);
            moduleIds.add(node.getId());
            for (String moduleId : moduleIds) {
                if (!allModuleIdList.contains(moduleId)) {
                    allModuleIdList.add(moduleId);
                }
            }
        }
        request.setModuleIds(allModuleIdList);

        List<Map<String, Object>> moduleCountList = getModuleCountFunc.apply(request);
//        List<Map<String,Object>> moduleCountList = extTestCaseMapper.moduleCountByCollection(request);

        Map<String, Integer> moduleCountMap = this.parseModuleCountList(moduleCountList);
        moduleNodes.forEach(node -> {
            List<String> moduleIds = new ArrayList<>();
            moduleIds = nodeList(moduleNodes, node.getId(), moduleIds);
            moduleIds.add(node.getId());
            int countNum = 0;
            for (String moduleId : moduleIds) {
                if (moduleCountMap.containsKey(moduleId)) {
                    countNum += moduleCountMap.get(moduleId).intValue();
                }
            }
            node.setCaseNum(countNum);
        });
    }

    private Map<String, Integer> parseModuleCountList(List<Map<String, Object>> moduleCountList) {
        Map<String, Integer> returnMap = new HashMap<>();
        for (Map<String, Object> map : moduleCountList) {
            Object moduleIdObj = map.get("moduleId");
            Object countNumObj = map.get("countNum");
            if (moduleIdObj != null && countNumObj != null) {
                String moduleId = String.valueOf(moduleIdObj);
                try {
                    Integer countNumInteger = new Integer(String.valueOf(countNumObj));
                    returnMap.put(moduleId, countNumInteger);
                } catch (Exception e) {
                }
            }
        }
        return returnMap;
    }

    public List<String> nodeList(List<ModuleNodeDTO> testCaseNodes, String pid, List<String> list) {
        for (ModuleNodeDTO node : testCaseNodes) {
            //????????????id???????????????id???add??????????????????
            if (StringUtils.equals(node.getParentId(), pid)) {
                list.add(node.getId());
                //?????????????????????
                nodeList(testCaseNodes, node.getId(), list);
            }
        }
        return list;
    }

    public int editNode(DragNodeRequest request) {
        return editNodeAndNodePath(request, null);
    }

    protected int editNodeAndNodePath(DragNodeRequest request, Consumer<List<String>> editNodePathFunc) {
        request.setUpdateTime(System.currentTimeMillis());
        checkTestCaseNodeExist(request);
        if (!CollectionUtils.isEmpty(request.getNodeIds()) && editNodePathFunc != null) {
            editNodePathFunc.accept(request.getNodeIds());
        }
        return extModuleNodeMapper.updateByPrimaryKeySelective(tableName, request);
    }

    /**
     * nodeIds ?????????????????????ID?????????????????????ID
     *
     * @param nodeIds
     * @param deleteNodeDataFunc
     * @return
     */
    protected int deleteNode(List<String> nodeIds, Consumer<List<String>> deleteNodeDataFunc) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return 1;
        }

        if (deleteNodeDataFunc != null) {
            // ??????node????????????
            deleteNodeDataFunc.accept(nodeIds);
        }

        TestCaseNodeExample testCaseNodeExample = new TestCaseNodeExample();
        testCaseNodeExample.createCriteria().andIdIn(nodeIds);
        return extModuleNodeMapper.deleteByExample(tableName, testCaseNodeExample);
    }

    public List<ModuleNodeDTO> getNodeTreeWithPruningTree(Map<String, List<String>> projectNodeMap) {
        List<ModuleNodeDTO> list = new ArrayList<>();
        projectNodeMap.forEach((k, v) -> {
            Project project = projectMapper.selectByPrimaryKey(k);
            if (project != null) {
                String name = project.getName();
                List<ModuleNodeDTO> moduleNodes = getNodeTreeWithPruningTree(k, v);
                ModuleNodeDTO moduleNodeDTO = new ModuleNodeDTO();
                moduleNodeDTO.setId(project.getId());
                moduleNodeDTO.setName(name);
                moduleNodeDTO.setLabel(name);
                moduleNodeDTO.setChildren(moduleNodes);
                if (!CollectionUtils.isEmpty(moduleNodes)) {
                    list.add(moduleNodeDTO);
                }
            }
        });
        return list;
    }

    /**
     * ????????????????????????
     *
     * @param projectId
     * @param pruningTreeIds
     * @return
     */
    public List<ModuleNodeDTO> getNodeTreeWithPruningTree(String projectId, List<String> pruningTreeIds) {
        List<ModuleNodeDTO> testCaseNodes = extModuleNodeMapper.getNodeTreeByProjectId(tableName, projectId);
        List<ModuleNodeDTO> nodeTrees = getNodeTrees(testCaseNodes);
        Iterator<ModuleNodeDTO> iterator = nodeTrees.iterator();
        while (iterator.hasNext()) {
            ModuleNodeDTO rootNode = iterator.next();
            if (pruningTree(rootNode, pruningTreeIds)) {
                iterator.remove();
            }
        }
        return nodeTrees;
    }

    public Map<String, String> createNodeByTestCases(List<TestCaseWithBLOBs> testCases, String projectId, String defaultName) {
        List<String> nodePaths = testCases.stream()
                .map(TestCase::getNodePath)
                .collect(Collectors.toList());
        return this.createNodes(nodePaths, projectId, defaultName);
    }

    public Map<String, String> createNodes(List<String> nodePaths, String projectId, String defaultName) {
        List<ModuleNodeDTO> nodeTrees = getNodeTreeByProjectId(projectId, defaultName);
        Map<String, String> pathMap = new HashMap<>();
        for (String item : nodePaths) {
            if (item == null) {
                throw new ExcelException(Translator.get("test_case_module_not_null"));
            }
            List<String> nodeNameList = new ArrayList<>(Arrays.asList(item.split("/")));
            Iterator<String> itemIterator = nodeNameList.iterator();
            Boolean hasNode = false;
            String rootNodeName;

            if (nodeNameList.size() <= 1) {
                throw new ExcelException(Translator.get("test_case_create_module_fail") + ":" + item);
            } else {
                itemIterator.next();
                itemIterator.remove();
                rootNodeName = itemIterator.next().trim();
                //???????????????????????????nodeTrees????????????
                for (ModuleNodeDTO nodeTree : nodeTrees) {
                    if (StringUtils.equals(rootNodeName, nodeTree.getName())) {
                        hasNode = true;
                        createNodeByPathIterator(itemIterator, "/" + rootNodeName, nodeTree,
                                pathMap, projectId, 2);
                    }
                    ;
                }
            }
            if (!hasNode) {
                createNodeByPath(itemIterator, rootNodeName, null, projectId, 1, "", pathMap);
            }
        }
        return pathMap;

    }

    @Override
    public String insertNode(String nodeName, String pId, String projectId, Integer level) {
        ModuleNode moduleNode = new ModuleNode();
        moduleNode.setName(nodeName.trim());
        moduleNode.setParentId(pId);
        moduleNode.setProjectId(projectId);
        moduleNode.setCreateTime(System.currentTimeMillis());
        moduleNode.setUpdateTime(System.currentTimeMillis());
        moduleNode.setLevel(level);
        moduleNode.setCreateUser(SessionUtils.getUserId());
        moduleNode.setId(UUID.randomUUID().toString());
        double pos = getNextLevelPos(projectId, level, pId);
        moduleNode.setPos(pos);
        extModuleNodeMapper.insert(tableName, moduleNode);
        return moduleNode.getId();
    }

    public void dragNode(DragNodeRequest request) {
        dragNodeAndDataEdit(request, null, null);
    }

    /**
     * ??????????????????????????????????????????????????? modulePath ??????
     *
     * @param request
     * @param getNodeDataFunc  ?????? nodeIds ???????????????????????????
     * @param editNodeDataFunc ?????? modulePath ?????????
     */
    protected void dragNodeAndDataEdit(DragNodeRequest request,
                                       Function<List<String>, List<EditModuleDateDTO>> getNodeDataFunc,
                                       Consumer<List<EditModuleDateDTO>> editNodeDataFunc) {

        if (request.getNodeTree() == null) {
            return;
        }

        checkTestCaseNodeExist(request);

        List<String> nodeIds = request.getNodeIds();
        TestCaseNodeDTO nodeTree = request.getNodeTree();
        List<ModuleNode> updateNodes = new ArrayList<>();

        if (getNodeDataFunc != null && editNodeDataFunc != null) {
            List<EditModuleDateDTO> nodeData = getNodeDataFunc.apply(nodeIds);
            buildUpdateTestCase(nodeTree, nodeData, updateNodes, "/", "0", 1);
            editNodeDataFunc.accept(nodeData);
        } else {
            buildUpdateModule(nodeTree, updateNodes, "0", 1);
        }

        updateNodes = updateNodes.stream()
                .filter(item -> nodeIds.contains(item.getId()))
                .collect(Collectors.toList());

        batchUpdateTestCaseNode(updateNodes);
    }

    private void batchUpdateTestCaseNode(List<ModuleNode> updateNodes) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        ExtModuleNodeMapper extModuleNodeMapper = sqlSession.getMapper(ExtModuleNodeMapper.class);
        updateNodes.forEach((value) -> {
            extModuleNodeMapper.updateByPrimaryKeySelective(tableName, value);
        });
        sqlSession.flushStatements();
        if (sqlSession != null && sqlSessionFactory != null) {
            SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
        }
    }

    private void buildUpdateModule(TestCaseNodeDTO rootNode,
                                   List<ModuleNode> updateNodes, String pId, int level) {
        checkoutNodeLimit(level);

        ModuleNode moduleNode = new ModuleNode();
        moduleNode.setId(rootNode.getId());
        moduleNode.setLevel(level);
        moduleNode.setParentId(pId);
        updateNodes.add(moduleNode);

        List<TestCaseNodeDTO> children = rootNode.getChildren();
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                buildUpdateModule(children.get(i), updateNodes, rootNode.getId(), level + 1);
            }
        }
    }

    public void checkoutNodeLimit(int level) {
        if (level > TestCaseConstants.MAX_NODE_DEPTH) {
            MSException.throwException(Translator.get("node_deep_limit"));
        }
    }

    private void buildUpdateTestCase(TestCaseNodeDTO rootNode, List<EditModuleDateDTO> nodeData,
                                     List<ModuleNode> updateNodes, String rootPath, String pId, int level) {

        rootPath = rootPath + rootNode.getName();

        checkoutNodeLimit(level);

        ModuleNode moduleNode = new ModuleNode();
        moduleNode.setId(rootNode.getId());
        moduleNode.setLevel(level);
        moduleNode.setParentId(pId);
        updateNodes.add(moduleNode);

        for (EditModuleDateDTO item : nodeData) {
            if (StringUtils.equals(item.getModuleId(), rootNode.getId())) {
                item.setModulePath(rootPath);
            }
        }

        List<TestCaseNodeDTO> children = rootNode.getChildren();
        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                buildUpdateTestCase(children.get(i), nodeData, updateNodes, rootPath + '/', rootNode.getId(), level + 1);
            }
        }
    }

    @Override
    public ModuleNodeDTO getNode(String id) {
        return extModuleNodeMapper.get(tableName, id);
    }

    @Override
    public void updatePos(String id, Double pos) {
        extModuleNodeMapper.updatePos(tableName, id, pos);
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param projectId ???????????? id
     * @param level     node level
     * @param parentId  node parent id
     * @param order     pos ????????????
     * @return ???????????????????????????????????????????????????
     */
    private List<ModuleNode> getPos(String projectId, int level, String parentId, String order) {
        TestCaseNodeExample example = new TestCaseNodeExample();
        TestCaseNodeExample.Criteria criteria = example.createCriteria();
        criteria.andProjectIdEqualTo(projectId).andLevelEqualTo(level);
        if (level != 1 && StringUtils.isNotBlank(parentId)) {
            criteria.andParentIdEqualTo(parentId);
        }
        example.setOrderByClause(order);
        return extModuleNodeMapper.selectByExample(tableName, example);
    }

    /**
     * ????????????????????? pos ???
     *
     * @param projectId project id
     * @param level     node level
     * @param parentId  node parent id
     */
    @Override
    protected void refreshPos(String projectId, int level, String parentId) {
        List<ModuleNode> nodes = getPos(projectId, level, parentId, "pos asc");
        if (!CollectionUtils.isEmpty(nodes)) {
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            ExtModuleNodeMapper extModuleNodeMapper = sqlSession.getMapper(ExtModuleNodeMapper.class);
            AtomicDouble pos = new AtomicDouble(DEFAULT_POS);
            nodes.forEach((node) -> {
                node.setPos(pos.getAndAdd(DEFAULT_POS));
                extModuleNodeMapper.updateByPrimaryKey(tableName, node);
            });
            sqlSession.flushStatements();
            if (sqlSession != null && sqlSessionFactory != null) {
                SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
            }
        }
    }


    /**
     * ??????????????????????????? pos ???
     *
     * @param projectId project id
     * @param level     node level
     * @param parentId  node parent id
     * @return ????????????????????? pos ???
     */
    private double getNextLevelPos(String projectId, int level, String parentId) {
        List<ModuleNode> list = getPos(projectId, level, parentId, "pos desc");
        if (!CollectionUtils.isEmpty(list) && list.get(0) != null && list.get(0).getPos() != null) {
            return list.get(0).getPos() + DEFAULT_POS;
        } else {
            return DEFAULT_POS;
        }
    }

    public String getLogDetails(List<String> ids) {
        TestCaseNodeExample example = new TestCaseNodeExample();
        example.createCriteria().andIdIn(ids);
        List<ModuleNode> nodes = extModuleNodeMapper.selectByExample(tableName, example);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(nodes)) {
            List<String> names = nodes.stream().map(TestCaseNode::getName).collect(Collectors.toList());
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(ids), nodes.get(0).getProjectId(), String.join(",", names), nodes.get(0).getCreateUser(), new LinkedList<>());
            return JSON.toJSONString(details);
        }
        return null;
    }

    public String getLogDetails(ModuleNode node) {
        ModuleNode module = null;
        if (StringUtils.isNotEmpty(node.getId())) {
            module = extModuleNodeMapper.selectByPrimaryKey(tableName, node.getId());
        }
        if (module == null && StringUtils.isNotEmpty(node.getName())) {
            TestCaseNodeExample example = new TestCaseNodeExample();
            TestCaseNodeExample.Criteria criteria = example.createCriteria();
            criteria.andNameEqualTo(node.getName()).andProjectIdEqualTo(node.getProjectId());
            if (StringUtils.isNotEmpty(node.getParentId())) {
                criteria.andParentIdEqualTo(node.getParentId());
            } else {
                criteria.andParentIdIsNull();
            }
            if (StringUtils.isNotEmpty(node.getId())) {
                criteria.andIdNotEqualTo(node.getId());
            }
            List<ModuleNode> list = extModuleNodeMapper.selectByExample(tableName, example);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {
                module = list.get(0);
            }
        }
        if (module != null) {
            List<DetailColumn> columns = ReflexObjectUtil.getColumns(module, ModuleReference.moduleColumns);
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(module.getId()), module.getProjectId(), module.getCreateUser(), columns);
            return JSON.toJSONString(details);
        }
        return null;
    }

    public long countById(String nodeId) {
        TestCaseNodeExample example = new TestCaseNodeExample();
        example.createCriteria().andIdEqualTo(nodeId);
        return extModuleNodeMapper.countByExample(tableName, example);
    }

    public List<ModuleNode> selectSameModule(ModuleNode node) {
        TestCaseNodeExample example = new TestCaseNodeExample();
        TestCaseNodeExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(node.getName())
                .andProjectIdEqualTo(node.getProjectId())
                .andLevelEqualTo(node.getLevel());

        if (StringUtils.isNotBlank(node.getId())) {
            criteria.andIdNotEqualTo(node.getId());
        }
        return extModuleNodeMapper.selectByExample(tableName, example);
    }
}
