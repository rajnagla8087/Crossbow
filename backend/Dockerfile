# ==========================================
# Production Multi-Stage Dockerfile
# ==========================================
FROM node:20-alpine AS base

# Install curl for container health check
RUN apk add --no-cache curl

WORKDIR /app

# Install dependencies with clean cache
COPY package*.json ./
RUN npm ci --only=production && npm cache clean --force

# Copy application source & static assets
COPY . .

# Run as non-privileged node user for security
USER node

# Production environment variables
ENV NODE_ENV=production
ENV PORT=3000

EXPOSE 3000

# Container Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3000/api/health || exit 1

CMD ["node", "server.js"]
