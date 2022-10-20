<template>
    <div>
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="user_id" label="Student id" width="180">
        </el-table-column>
        <el-table-column prop="user_name" label="Student name" width="180">
        </el-table-column>
        <el-table-column prop="token_amount" label="Student tokens" width="180">
        </el-table-column>
        <el-table-column prop="user_email" label="Student Email" width="180">
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
          url: this.$http.adornUrl('/token/students'),
          method: 'get',
  
        }).then(({ data }) => {
          this.tableData = data.result
        })
      }
    },
    created() {  },
    mounted() { this.getCourses()
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