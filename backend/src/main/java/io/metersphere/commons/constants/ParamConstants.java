package io.metersphere.commons.constants;


public interface ParamConstants {

    String getValue();

    enum Type implements ParamConstants {

        PASSWORD("password"),
        TEXT("text"),
        JSON("json");

        private String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    enum Classify implements ParamConstants {
        MAIL("smtp"),
        BASE("base"),
        LDAP("ldap"),
        THIRD_PARTY_AUTH("third_party_auth"),
        REGISTRY("registry");

        private String value;

        Classify(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    enum Registry implements ParamConstants {
        URL("registry.url"),
        REPO("registry.repo"),
        USERNAME("registry.username"),
        PASSWORD("registry.password");

        private String value;

        Registry(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    enum I18n implements ParamConstants {

        LANGUAGE("i18n.language");

        private String value;

        I18n(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    enum MAIL implements ParamConstants {
        SERVER("smtp.host"),
        PORT("smtp.port"),
        ACCOUNT("smtp.account"),
        PASSWORD("smtp.password"),
        SSL("smtp.ssl"),
        TLS("smtp.tls"),
        RECIPIENTS("smtp.recipient");

        private String value;

        private MAIL(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }

    enum BASE implements ParamConstants {
        URL("base.url"),
        CONCURRENCY("base.concurrency"),
        PROMETHEUS_HOST("base.prometheus.host"),
        SELENIUM_DOCKER_URL("base.selenium.docker.url");

        private String value;

        private BASE(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    enum LDAP implements ParamConstants {
        URL("ldap.url"),
        DN("ldap.dn"),
        PASSWORD("ldap.password"),
        OU("ldap.ou"),
        FILTER("ldap.filter"),
        MAPPING("ldap.mapping"),
        OPEN("ldap.open");

        private String value;

        LDAP(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    enum THIRD_PARTY_AUTH implements ParamConstants {
        JIRA_ADDRESS("third_party_auth.jira_address"),
        JIRA_USERNAME("third_party_auth.jira_username"),
        JIRA_PASSWORD("third_party_auth.jira_password"),
        JENKINS_USERNAME("third_party_auth.jenkins_username"),
        JENKINS_PASSWORD("third_party_auth.jenkins_password");

        private String value;

        THIRD_PARTY_AUTH(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
