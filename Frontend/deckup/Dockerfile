# Etapa de build
FROM node:20-alpine AS builder
WORKDIR /app

# Copiamos los archivos necesarios
COPY package*.json ./
COPY angular.json ./
COPY tsconfig*.json ./
COPY . .

# Instalamos dependencias y construimos
RUN npm install && npm run build -- --configuration=production

# Etapa de producción con Nginx
FROM nginx
COPY --from=builder /app/dist/deckup /app/dist/deckup

# Copiamos configuración custom de nginx si tienes
# COPY nginx.conf /etc/nginx/nginx.conf
