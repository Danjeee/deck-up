FROM nginx:alpine

RUN adduser -S -G www-data www-data

COPY nginx.conf /etc/nginx/nginx.conf
COPY ./ssl-certs/deckup.crt /etc/nginx/deckup.crt
COPY ./ssl-certs/deckup.key /etc/nginx/deckup.key

WORKDIR /var/www/html

RUN chown -R www-data:www-data /var/www/html/

EXPOSE 444