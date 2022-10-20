<template>
  <div>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="assignment_id" label="Course id" width="180">
      </el-table-column>
      <el-table-column prop="assignment_name" label="Cousse name" width="180">
      </el-table-column>
    </el-table>
  </div>
</template>

<script>

export default {
  components: {},
  props: {},
  data() {
    return {
      tableData: []
    };
  },
  computed: {},
  watch: {},
  methods: {
    getCourses() {
      this.$http({
        url: this.$http.adornUrl('/token/courses'),
        method: 'get',

      }).then(({ data }) => {
        this.tableData = data.result
      })
    }
  },
  created() { this.getCourses() },
  mounted() {
  },
  beforeCreated() { },
  beforeMounted() { },
  beforeUpdate() { },
  updated() { },
  beforeDestory() { },
  destoryed() { },
  actived() { }
}

</script>
<style lang='css' scoped>

</style>