<template>
  <div v-loading="result.loading">
<!--    <el-image :src="src" class="opacity_img"></el-image>-->
  </div>
</template>

<script>

export default {
  name: "dataHome",
  data() {
    return {
      src: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg',
      result: {}
    };
  },
}
</script>

<style scoped>
.opacity_img {
  width:100%;
  height:100%;
  opacity: 0.4;
  background-size: cover;
  background-repeat: no-repeat;
}
</style>
