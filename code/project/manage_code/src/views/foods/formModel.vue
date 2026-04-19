<template>
  <el-form
    :model="form"
    :rules="rules"
    ref="formRef"
    label-width="120px"
    class="foods_form"
  >
    <el-row>
      <el-col :span="12">
        <el-form-item label="食材编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入食材编码"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="食材名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入食材名称"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <el-form-item label="热量(kcal/100g)" prop="calory">
          <el-input-number
            v-model="form.calory"
            :min="0"
            :step="0.1"
            placeholder="请输入热量"
          ></el-input-number>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="重量" prop="weight">
          <el-input-number
            v-model="form.weight"
            :min="0"
            :step="1"
            placeholder="请输入重量"
          ></el-input-number>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <el-form-item label="单位" prop="unit">
          <el-select v-model="form.unit" placeholder="请选择单位">
            <el-option label="g" value="g"></el-option>
            <el-option label="kg" value="kg"></el-option>
            <el-option label="ml" value="ml"></el-option>
            <el-option label="l" value="l"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="图片" prop="thumbImageUrl">
          <upload
            :limit="1"
            @change="uploadChange"
            type="image"
            :value="form.thumbImageUrl"
          ></upload>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from "vue";
import upload from "@/components/common/upload.vue";

const props = defineProps({
  form: {
    type: Object,
    default: () => ({}),
  },
  rules: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(["ok"]);

const formRef = ref(null);

// 验证
const validate = (callback) => {
  formRef.value.validate(callback);
};

// 上传回调
const uploadChange = (value) => {
  props.form.thumbImageUrl = value;
};

// 暴露方法
defineExpose({
  validate,
});
</script>

<style scoped>
.foods_form {
  max-width: 100%;
}

.foods_form .el-form-item {
  margin-bottom: 16px;
}

.foods_form .el-form-item__label {
  font-weight: 500;
  color: #495057;
}

.foods_form .el-input,
.foods_form .el-select,
.foods_form .el-input-number {
  border-radius: 4px;
  border: 1px solid #ced4da;
  transition: all 0.3s ease;
}

.foods_form .el-input:focus,
.foods_form .el-select:focus,
.foods_form .el-input-number:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}
</style>
