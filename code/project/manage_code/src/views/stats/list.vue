<template>
  <div class="stats_view">
    <!-- 顶部数字卡片 -->
    <div class="stats_cards">
      <div class="stat_card">
        <div class="stat_card_icon users">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat_card_content">
          <div class="stat_card_number">{{ totalUsers }}</div>
          <div class="stat_card_label">总用户数</div>
        </div>
      </div>
      <div class="stat_card">
        <div class="stat_card_icon today">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat_card_content">
          <div class="stat_card_number">{{ todayUsers }}</div>
          <div class="stat_card_label">今日新增</div>
        </div>
      </div>
      <div class="stat_card">
        <div class="stat_card_icon month">
          <el-icon><Calendar /></el-icon>
        </div>
        <div class="stat_card_content">
          <div class="stat_card_number">{{ monthUsers }}</div>
          <div class="stat_card_label">本月新增</div>
        </div>
      </div>
    </div>

    <!-- 饼图区域 -->
    <div class="charts_container">
      <!-- 性别分布 -->
      <div class="chart_card">
        <div class="chart_title">用户性别占比</div>
        <div ref="genderChartRef" class="chart"></div>
      </div>

      <!-- 年龄分布 -->
      <div class="chart_card">
        <div class="chart_title">用户年龄分布</div>
        <div ref="ageChartRef" class="chart"></div>
      </div>

      <!-- 健身目标分布 -->
      <div class="chart_card">
        <div class="chart_title">用户健身目标分布</div>
        <div ref="goalChartRef" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { User, Timer, Calendar } from "@element-plus/icons-vue";
import http from "@/utils/http";

// 数字卡片数据
const totalUsers = ref(0);
const todayUsers = ref(0);
const monthUsers = ref(0);

// 饼图引用
const genderChartRef = ref(null);
const ageChartRef = ref(null);
const goalChartRef = ref(null);

// 图表实例
let genderChart = null;
let ageChart = null;
let goalChart = null;

// 初始化图表
const initCharts = () => {
  // 性别分布饼图
  if (genderChartRef.value) {
    genderChart = echarts.init(genderChartRef.value);
    genderChart.setOption({
      tooltip: {
        trigger: "item",
        formatter: "{a} <br/>{b}: {c} ({d}%)",
      },
      legend: {
        orient: "vertical",
        left: "left",
        data: ["男", "女"],
      },
      series: [
        {
          name: "性别",
          type: "pie",
          radius: ["40%", "70%"],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: "#fff",
            borderWidth: 2,
          },
          label: {
            show: false,
            position: "center",
          },
          emphasis: {
            label: {
              show: true,
              fontSize: "18",
              fontWeight: "bold",
            },
          },
          labelLine: {
            show: false,
          },
          data: [
            { value: 16, name: "男" },
            { value: 15, name: "女" },
          ],
          color: ["#4CAF50", "#2196F3"],
        },
      ],
    });
  }

  // 年龄分布饼图
  if (ageChartRef.value) {
    ageChart = echarts.init(ageChartRef.value);
    ageChart.setOption({
      tooltip: {
        trigger: "item",
        formatter: "{a} <br/>{b}: {c} ({d}%)",
      },
      legend: {
        orient: "vertical",
        left: "left",
        data: ["18-25岁", "26-35岁", "36-45岁", "46岁以上"],
      },
      series: [
        {
          name: "年龄",
          type: "pie",
          radius: ["40%", "70%"],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: "#fff",
            borderWidth: 2,
          },
          label: {
            show: false,
            position: "center",
          },
          emphasis: {
            label: {
              show: true,
              fontSize: "18",
              fontWeight: "bold",
            },
          },
          labelLine: {
            show: false,
          },
          data: [
            { value: 10, name: "18-25岁" },
            { value: 15, name: "26-35岁" },
            { value: 5, name: "36-45岁" },
            { value: 1, name: "46岁以上" },
          ],
          color: ["#FF9800", "#E91E63", "#9C27B0", "#673AB7"],
        },
      ],
    });
  }

  // 健身目标分布饼图
  if (goalChartRef.value) {
    goalChart = echarts.init(goalChartRef.value);
    goalChart.setOption({
      tooltip: {
        trigger: "item",
        formatter: "{a} <br/>{b}: {c} ({d}%)",
      },
      legend: {
        orient: "vertical",
        left: "left",
        data: ["增肌", "减脂", "维持"],
      },
      series: [
        {
          name: "健身目标",
          type: "pie",
          radius: ["40%", "70%"],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: "#fff",
            borderWidth: 2,
          },
          label: {
            show: false,
            position: "center",
          },
          emphasis: {
            label: {
              show: true,
              fontSize: "18",
              fontWeight: "bold",
            },
          },
          labelLine: {
            show: false,
          },
          data: [
            { value: 10, name: "增肌" },
            { value: 16, name: "减脂" },
            { value: 5, name: "维持" },
          ],
          color: ["#4CAF50", "#FF5722", "#FFC107"],
        },
      ],
    });
  }
};

// 监听窗口大小变化
const handleResize = () => {
  if (genderChart) genderChart.resize();
  if (ageChart) ageChart.resize();
  if (goalChart) goalChart.resize();
};

// 更新性别分布图表
const updateGenderChart = (data) => {
  if (genderChart) {
    const legendData = data.map((item) => item.name);
    // 为未知类别设置灰色
    const processedData = data.map((item) => {
      if (item.name === "未知") {
        return { ...item, itemStyle: { color: "#999999" } };
      }
      return item;
    });
    genderChart.setOption({
      legend: {
        data: legendData,
      },
      series: [
        {
          data: processedData,
        },
      ],
    });
  }
};

// 更新年龄分布图表
const updateAgeChart = (data) => {
  if (ageChart) {
    const legendData = data.map((item) => item.name);
    // 为未知类别设置灰色
    const processedData = data.map((item) => {
      if (item.name === "未知") {
        return { ...item, itemStyle: { color: "#999999" } };
      }
      return item;
    });
    ageChart.setOption({
      legend: {
        data: legendData,
      },
      series: [
        {
          data: processedData,
        },
      ],
    });
  }
};

// 更新健身目标分布图表
const updateGoalChart = (data) => {
  if (goalChart) {
    const legendData = data.map((item) => item.name);
    // 为未知类别设置灰色
    const processedData = data.map((item) => {
      if (item.name === "未知") {
        return { ...item, itemStyle: { color: "#999999" } };
      }
      return item;
    });
    goalChart.setOption({
      legend: {
        data: legendData,
      },
      series: [
        {
          data: processedData,
        },
      ],
    });
  }
};

// 从后端API获取真实数据
const loadData = async () => {
  try {
    // 调用后端API获取用户统计数据
    const response = await http.get("/stats/userStats");
    const data = response.data;
    console.log("用户统计数据:", data);

    if (data.code === 0) {
      totalUsers.value = data.totalUsers;
      todayUsers.value = data.todayUsers || 0;
      monthUsers.value = data.monthUsers || 0;
    }

    // 调用后端API获取性别分布数据
    const genderResponse = await http.get("/stats/userGenderStats");
    const genderData = genderResponse.data;
    console.log("性别分布数据:", genderData);

    if (genderData.code === 0) {
      console.log("性别分布详细数据:", genderData.data);
      updateGenderChart(genderData.data);
    }

    // 调用后端API获取年龄分布数据
    const ageResponse = await http.get("/stats/userAgeStats");
    const ageData = ageResponse.data;
    console.log("年龄分布数据:", ageData);

    if (ageData.code === 0) {
      console.log("年龄分布详细数据:", ageData.data);
      updateAgeChart(ageData.data);
    }

    // 调用后端API获取健身目标分布数据
    const goalResponse = await http.get("/stats/userGoalStats");
    const goalData = goalResponse.data;
    console.log("健身目标分布数据:", goalData);

    if (goalData.code === 0) {
      console.log("健身目标分布详细数据:", goalData.data);
      updateGoalChart(goalData.data);
    }
  } catch (error) {
    console.error("获取统计数据失败:", error);
    // 设置默认数据
    totalUsers.value = 31;
    todayUsers.value = 5;
    monthUsers.value = 15;
    // 默认性别分布
    updateGenderChart([
      { value: 16, name: "男" },
      { value: 15, name: "女" },
    ]);
    // 默认年龄分布
    updateAgeChart([
      { value: 10, name: "18-25岁" },
      { value: 15, name: "26-35岁" },
      { value: 5, name: "36-45岁" },
      { value: 1, name: "46岁以上" },
    ]);
    // 默认健身目标分布
    updateGoalChart([
      { value: 10, name: "增肌" },
      { value: 16, name: "减脂" },
      { value: 5, name: "维持" },
    ]);
  }
};

onMounted(() => {
  loadData();
  initCharts();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  if (genderChart) genderChart.dispose();
  if (ageChart) ageChart.dispose();
  if (goalChart) goalChart.dispose();
});
</script>

<style lang="scss" scoped>
.stats_view {
  padding: 20px;
}

.section_title {
  width: 100%;
  margin: 0px 0 20px;
  text-align: left;
  background: none;
  margin-left: -10px;
  border-bottom: 1px #e5e5e5 solid;
  position: relative;

  span {
    display: inline-block;
    font-size: 20px;
    background: var(--theme);
    color: #fff;
    font-weight: 500;
    padding: 15px 20px 15px 20px;
    min-width: 250px;
  }

  &:after {
    content: "";
    height: 1px;
    width: 1px;
    border-style: solid;
    border-width: 5px;
    position: absolute;
    bottom: -10px;
    left: 0;
    border-color: #222 #222 transparent transparent;
  }
}

.stats_cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat_card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  }

  .stat_card_icon {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    color: #fff;

    &.users {
      background: linear-gradient(135deg, #4caf50, #45a049);
    }

    &.today {
      background: linear-gradient(135deg, #2196f3, #1976d2);
    }

    &.month {
      background: linear-gradient(135deg, #ff9800, #f57c00);
    }
  }

  .stat_card_content {
    flex: 1;

    .stat_card_number {
      font-size: 32px;
      font-weight: bold;
      color: #333;
      margin-bottom: 4px;
    }

    .stat_card_label {
      font-size: 14px;
      color: #666;
    }
  }
}

.charts_container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.chart_card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .chart_title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    text-align: center;
  }

  .chart {
    height: 300px;
  }
}
</style>
