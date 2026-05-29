FROM node:20-alpine AS build

WORKDIR /app

# 1. Copie des fichiers de dépendances
COPY package*.json ./

# 2. Installation
RUN rm -rf node_modules package-lock.json && \
    npm cache clean --force && \
    npm install

# 3. Copie du code source
COPY . .

# 4. Build
RUN npm run build --prod

# === Stage final ===
FROM nginx:alpine

COPY --from=build /app/dist/microservice-ui/browser /usr/share/nginx/html

# Configuration Nginx pour écouter sur 8080 + SPA
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
