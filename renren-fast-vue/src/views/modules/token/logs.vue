<template>
    <div>
        <el-table :data="tableData" border style="width: 100%">
            <el-table-column prop="id" label="Log id">
            </el-table-column>
            <el-table-column prop="user_name" label="Student name">
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
               tableData: [{id: 1, user_name: "zhangsan"}, {id: 2, user_name:"lisi"}] 
        };
    },
    computed: {},
    watch: {},
    methods: {},
    created() { },
    mounted() { },
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