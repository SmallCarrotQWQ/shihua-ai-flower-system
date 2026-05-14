<template>
  <div class="admin">
    <aside>
      <h1>拾花后台</h1>
      <RouterLink to="/admin/dashboard">数据看板</RouterLink>
      <RouterLink to="/admin/flower">鲜花管理</RouterLink>
      <RouterLink to="/admin/category">分类管理</RouterLink>
      <RouterLink to="/admin/order">订单管理</RouterLink>
      <RouterLink to="/admin/user">用户管理</RouterLink>
      <RouterLink to="/home">返回商城</RouterLink>
    </aside>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.admin {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 220px 1fr;
}

aside {
  background: #101828;
  color: #ffffff;
  padding: 24px;
}

h1 {
  margin: 0 0 24px;
  font-size: 22px;
}

a {
  display: block;
  padding: 10px 0;
  color: #d0d5dd;
}

main {
  padding: 28px;
}
</style>
