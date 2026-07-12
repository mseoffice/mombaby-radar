<template>
  <div class="app-shell">
    <nav class="side-nav">
      <div class="logo">📊 母婴雷达</div>
      <router-link to="/dashboard">Dashboard</router-link>
      <router-link to="/products">商品中心</router-link>
      <router-link to="/content-factory">内容工厂</router-link>
      <router-link to="/ai-ops">AI 运营中心</router-link>
      <router-link to="/private-ops">私域运营</router-link>
      <router-link to="/analytics">数据中心</router-link>
    </nav>
    <main class="content">
      <!-- 占位：按设计视角文档 6 页面清单落地 -->
      <router-view />
    </main>
  </div>
</template>

<script setup>
// 页面清单见 设计视角_页面与状态清单.md（P1–P6 + 路由草案）
// AI 生成内容须标注“AI 生成”标识（附录C / 附录B）
</script>

<style>
.app-shell { display: flex; min-height: 100vh; }
.side-nav { width: 200px; background: #1f2937; color: #fff; padding: 16px; }
.side-nav a { display: block; color: #cbd5e1; padding: 8px 0; text-decoration: none; }
.side-nav a.router-link-active { color: #fff; font-weight: 600; }
.content { flex: 1; padding: 24px; }
.logo { font-weight: 700; margin-bottom: 16px; }
</style>
