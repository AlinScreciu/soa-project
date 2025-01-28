# 1. Build stage
FROM node:18-alpine AS builder

WORKDIR /app
COPY . . 

# ARG to pick the Nx project name
ARG MFE_NAME=shell

# Build only that MFE
# e.g., if using Nx:
RUN npm install -g pnpm
RUN pnpm dlx nx build $MFE_NAME --configuration=production

# 2. Final stage: NGINX
FROM nginx:alpine

# Remove default config if you have a custom one
RUN rm /etc/nginx/conf.d/default.conf

# Copy your custom default.conf
COPY docker/default.conf /etc/nginx/conf.d/default.conf

# Copy only the chosen MFE’s dist output from the builder
ARG MFE_NAME=shell
COPY --from=builder /app/dist/apps/${MFE_NAME} /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
