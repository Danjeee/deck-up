docker exec -it nginx sh -c 'chown -R www-data:www-data /usr/share/nginx/static'
docker exec -it nginx sh -c 'chmod -R 775 /usr/share/nginx/static'