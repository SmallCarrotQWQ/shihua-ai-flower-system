<template>
  <section>
    <h1>数据看板</h1>
    <div class="grid">
      <div class="panel">
        <strong>今日订单</strong>
        <p>0</p>
      </div>
      <div class="panel">
        <strong>销售额</strong>
        <p>¥0.00</p>
      </div>
      <div class="panel">
        <strong>新增用户</strong>
        <p>0</p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

p {
  margin: 12px 0 0;
  font-size: 28px;
  font-weight: 700;
}
</style>

