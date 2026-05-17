<template>
  <section class="page">
    <h2>收货地址</h2>
    <div class="panel">
      <input v-model="form.receiver" placeholder="收货人" />
      <input v-model="form.phone" placeholder="手机号" />
      <input v-model="form.province" placeholder="省份" />
      <input v-model="form.city" placeholder="城市" />
      <input v-model="form.district" placeholder="区县" />
      <input v-model="form.detail" placeholder="详细地址" />
      <label><input v-model="defaultChecked" type="checkbox" /> 默认地址</label>
      <button type="button" @click="save">{{ editingId ? "保存修改" : "新增地址" }}</button>
    </div>

    <div v-for="item in addresses" :key="item.addressId" class="item">
      <strong>{{ item.receiver }} {{ item.phone }}</strong>
      <p>{{ item.province }} {{ item.city }} {{ item.district }} {{ item.detail }}</p>
      <small v-if="item.isDefault === 1">默认地址</small>
      <button type="button" @click="edit(item)">编辑</button>
      <button type="button" @click="setDefault(item.addressId)">设为默认</button>
      <button type="button" @click="remove(item.addressId)">删除</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import type { Address, AddressPayload } from "@/api/user";
import { createAddress, deleteAddress, getAddresses, setDefaultAddress, updateAddress } from "@/api/user";

const addresses = ref<Address[]>([]);
const editingId = ref<number | null>(null);
const defaultChecked = ref(false);
const form = reactive<AddressPayload>({
  receiver: "",
  phone: "",
  province: "",
  city: "",
  district: "",
  detail: "",
  isDefault: 0
});

onMounted(load);

async function load() {
  const response = await getAddresses();
  addresses.value = response.data.data;
}

async function save() {
  const payload = { ...form, isDefault: defaultChecked.value ? 1 : 0 };
  if (editingId.value) {
    await updateAddress(editingId.value, payload);
  } else {
    await createAddress(payload);
  }
  reset();
  await load();
}

function edit(item: Address) {
  editingId.value = item.addressId;
  Object.assign(form, item);
  defaultChecked.value = item.isDefault === 1;
}

async function setDefault(id: number) {
  await setDefaultAddress(id);
  await load();
}

async function remove(id: number) {
  await deleteAddress(id);
  await load();
}

function reset() {
  editingId.value = null;
  defaultChecked.value = false;
  Object.assign(form, { receiver: "", phone: "", province: "", city: "", district: "", detail: "", isDefault: 0 });
}
</script>

<style scoped>
.page {
  padding: 24px;
}
.panel {
  display: grid;
  gap: 10px;
  max-width: 520px;
  margin-bottom: 20px;
}
.item {
  border: 1px solid #e5e7eb;
  padding: 12px;
  margin-bottom: 10px;
}
button {
  margin-right: 8px;
}
</style>
