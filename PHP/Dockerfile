FROM php:8.2-fpm-alpine

WORKDIR /var/www/html

RUN chown -R www-data:www-data /var/www/html/

RUN chmod 664 /var/www/html/Resources/php/imagesend.php

EXPOSE 9000

CMD ["php-fpm"]