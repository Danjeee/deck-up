docker compose run --rm certbot certonly \
  --webroot \
  --webroot-path=/var/www/certbot \
  --email deckupgame@gmail.com \
  --agree-tos \
  --no-eff-email \
  -d deckup.tecnobyte.com
