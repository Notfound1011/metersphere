FROM nginx@sha256:7250923ba3543110040462388756ef099331822c6172a050b12c7a38361ea46f

MAINTAINER shiyuyu "yuyu.shi@cmexpro.com"

RUN rm /etc/nginx/conf.d/default.conf && apt-get update && apt-get install -y vim

ADD default.conf /etc/nginx/conf.d/

COPY dist/ /usr/share/nginx/html/
