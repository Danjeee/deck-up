FROM node:alpine

WORKDIR /web

COPY . .

RUN npm i -g @angular/cli@19

RUN npm i

EXPOSE 443

ENTRYPOINT ["ng", "serve", "--ssl", "--host", "0.0.0.0"]