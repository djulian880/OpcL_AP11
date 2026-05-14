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

COPY --from=build /app/dist/microservice-ui /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
