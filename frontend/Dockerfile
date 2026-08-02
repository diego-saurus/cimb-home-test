FROM node:22-bookworm-slim AS deps
WORKDIR /app
ENV CI=true
COPY package.json pnpm-lock.yaml* .npmrc* pnpm-workspace.yaml* ./
RUN corepack enable && corepack prepare pnpm@latest --activate \
 && pnpm install --frozen-lockfile 

FROM node:22-bookworm-slim AS build
WORKDIR /app
ENV CI=true
COPY --from=deps /app/node_modules ./node_modules
COPY . .
ARG VITE_SERVER_URL=/api
ENV VITE_SERVER_URL=${VITE_SERVER_URL}
RUN corepack enable && corepack prepare pnpm@latest --activate \
 && pnpm run build-only

FROM nginxinc/nginx-unprivileged:1.27-alpine AS runtime
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build --chown=nginx:nginx /app/dist /usr/share/nginx/html

USER nginx
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/healthz || exit 1

CMD ["nginx", "-g", "daemon off;"]
