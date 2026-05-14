# === Stage 1 : Build ===
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci --omit=dev

COPY . .
RUN npm run build --prod

# === Stage 2 : Production (Nginx) ===
FROM nginx:alpine

# Copie les fichiers buildés d'Angular
COPY --from=build /app/dist/<NOM_DE_TON_PROJET> /usr/share/nginx/html

# Copie une config nginx personnalisée (optionnel mais recommandé)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
