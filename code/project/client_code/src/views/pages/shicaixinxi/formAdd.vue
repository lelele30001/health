
<template>
	<div class="edit_view" :style='{}'>
        <div class="breadcrumb-wrapper" style="width: 100%;">
            <div class="bread_view">
                <el-breadcrumb separator="Ξ" class="breadcrumb">
                    <el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item class="second_breadcrumb" v-for="(item,index) in breadList" :key="index">{{item.name}}</el-breadcrumb-item>
                </el-breadcrumb>
            </div>
        </div>
		<el-form ref="formRef" :model="form" class="add_form" label-width="120px" :rules="rules">
			<el-row>
				<el-col :span="12">
					<el-form-item label="食材名称" prop="shicaimingcheng">
						<el-input class="list_inp" v-model="form.shicaimingcheng" placeholder="食材名称"
							 type="text" 							:readonly="!isAdd||disabledForm.shicaimingcheng?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="24">
					<el-form-item label="食材图片" prop="shicaitupian">
						<uploads
							:disabled="!isAdd||disabledForm.shicaitupian?true:false"
							action="file/upload"
							tip="请上传食材图片"
							style="width: 100%;text-align: left;"
							:fileUrls="form.shicaitupian?form.shicaitupian:''"
							@change="shicaitupianUploadSuccess">
						</uploads>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="食材类型" prop="shicaileixing">
						<el-select
							class="list_sel"
							:disabled="!isAdd||disabledForm.shicaileixing?true:false"
							v-model="form.shicaileixing"
							placeholder="请选择食材类型"
							style="width:100%;"
							>
							<el-option v-for="(item,index) in shicaileixingLists" :label="item"
								:value="item"
								>
							</el-option>
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="食材功效" prop="shicaigongxiao">
						<el-input class="list_inp" v-model="form.shicaigongxiao" placeholder="食材功效"
							 type="text" 							:readonly="!isAdd||disabledForm.shicaigongxiao?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item label="保存方式" prop="baocunfangshi">
						<el-input class="list_inp" v-model="form.baocunfangshi" placeholder="保存方式"
							 type="text" 							:readonly="!isAdd||disabledForm.baocunfangshi?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item label="食材用途" prop="shicaiyongtu">
						<el-input class="list_inp" v-model="form.shicaiyongtu" placeholder="食材用途"
							 type="text" 							:readonly="!isAdd||disabledForm.shicaiyongtu?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="24">
					<el-form-item label="食材详情" prop="shicaixiangqing">
						<editor class="list_editor" :value="form.shicaixiangqing" placeholder="请输入食材详情" :readonly="!isAdd||disabledForm.shicaixiangqing?true:false"
							@change="(e)=>editorChange(e,'shicaixiangqing')"></editor>
					</el-form-item>
				</el-col>
			</el-row>
			<div class="formModel_btn_box">
				<el-button class="formModel_cancel" @click="backClick">取消</el-button>
				<el-button class="formModel_confirm"
                           @click="save"
                           type="success"
				>
					保存
				</el-button>
			</div>
		</el-form>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		watch,
		onUnmounted,
		onMounted,
		nextTick,
		computed
	} from 'vue';
	import {
		useRoute,
		useRouter
	} from 'vue-router';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const route = useRoute()
	const router = useRouter()
	//基础信息
	const tableName = 'shicaixinxi'
	const formName = '食材信息'
	//基础信息
	const breadList = ref([{
		name: formName
	}])
	//获取唯一标识
	const getUUID =()=> {
      return new Date().getTime();
    }
	//form表单
	const form = ref({
		shicaimingcheng: '',
		shicaitupian: '',
		shicaileixing: '',
		shicaigongxiao: '',
		baocunfangshi: '',
		shicaiyongtu: '',
		shicaixiangqing: '',
		clicktime: '',
	})
	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
	const disabledForm = ref({
		shicaimingcheng : false,
		shicaitupian : false,
		shicaileixing : false,
		shicaigongxiao : false,
		baocunfangshi : false,
		shicaiyongtu : false,
		shicaixiangqing : false,
		clicktime : false,
		thumbsupNumber : false,
		crazilyNumber : false,
		storeupNumber : false,
		discussNumber : false,
		clickNumber : false,
	})
	const isAdd = ref(false)
	//表单验证
	const rules = ref({
		shicaimingcheng: [
			{required: true,message: '请输入',trigger: 'blur'},
		],
		shicaitupian: [
			{required: true,message: '请输入',trigger: 'blur'},
		],
		shicaileixing: [
		],
		shicaigongxiao: [
		],
		baocunfangshi: [
		],
		shicaiyongtu: [
		],
		shicaixiangqing: [
		],
		clicktime: [
		],
		thumbsupNumber: [
			{ validator: context.$toolUtil.validator.intNumber, trigger: 'blur' },
		],
		crazilyNumber: [
			{ validator: context.$toolUtil.validator.intNumber, trigger: 'blur' },
		],
		storeupNumber: [
			{ validator: context.$toolUtil.validator.intNumber, trigger: 'blur' },
		],
		discussNumber: [
			{ validator: context.$toolUtil.validator.intNumber, trigger: 'blur' },
		],
		clickNumber: [
			{ validator: context.$toolUtil.validator.intNumber, trigger: 'blur' },
		],
	})
	//食材图片上传回调
	const shicaitupianUploadSuccess=(e)=>{
		form.value.shicaitupian = e
	}
	//食材类型列表
	const shicaileixingLists = ref([])
	//methods

	//methods
	//获取info
	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			let reg=new RegExp('../../../file','g')
			res.data.data.shicaixiangqing = res.data.data.shicaixiangqing.replace(reg,'../../../project/file');
			form.value = res.data.data
		})
	}
	const crossRow = ref('')
	const crossTable = ref('')
	const crossTips = ref('')
	const crossColumnName = ref('')
	const crossColumnValue = ref('')
	//初始化
	const init = (formId=null,formType='add',formNames='',row=null,table=null,statusColumnName=null,tips=null,statusColumnValue=null) => {
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'add'){
			isAdd.value = true
		}else if(formType == 'info'){
			isAdd.value = false
			getInfo()
		}else if(formType == 'edit'){
			isAdd.value = true
			getInfo()
		}
		else if(formType == 'cross'){
			isAdd.value = true
			// getInfo()
			for(let x in row){
				if(x=='shicaimingcheng'){
					form.value.shicaimingcheng = row[x];
					disabledForm.value.shicaimingcheng = true;
					continue;
				}
				if(x=='shicaitupian'){
					form.value.shicaitupian = row[x];
					disabledForm.value.shicaitupian = true;
					continue;
				}
				if(x=='shicaileixing'){
					form.value.shicaileixing = row[x];
					disabledForm.value.shicaileixing = true;
					continue;
				}
				if(x=='shicaigongxiao'){
					form.value.shicaigongxiao = row[x];
					disabledForm.value.shicaigongxiao = true;
					continue;
				}
				if(x=='baocunfangshi'){
					form.value.baocunfangshi = row[x];
					disabledForm.value.baocunfangshi = true;
					continue;
				}
				if(x=='shicaiyongtu'){
					form.value.shicaiyongtu = row[x];
					disabledForm.value.shicaiyongtu = true;
					continue;
				}
				if(x=='shicaixiangqing'){
					form.value.shicaixiangqing = row[x];
					disabledForm.value.shicaixiangqing = true;
					continue;
				}
				if(x=='clicktime'){
					form.value.clicktime = row[x];
					disabledForm.value.clicktime = true;
					continue;
				}
				if(x=='thumbsupNumber'){
					form.value.thumbsupNumber = row[x];
					disabledForm.value.thumbsupNumber = true;
					continue;
				}
				if(x=='crazilyNumber'){
					form.value.crazilyNumber = row[x];
					disabledForm.value.crazilyNumber = true;
					continue;
				}
				if(x=='storeupNumber'){
					form.value.storeupNumber = row[x];
					disabledForm.value.storeupNumber = true;
					continue;
				}
				if(x=='discussNumber'){
					form.value.discussNumber = row[x];
					disabledForm.value.discussNumber = true;
					continue;
				}
				if(x=='clickNumber'){
					form.value.clickNumber = row[x];
					disabledForm.value.clickNumber = true;
					continue;
				}
			}
			if(row){
				crossRow.value = row
			}
			if(table){
				crossTable.value = table
			}
			if(tips){
				crossTips.value = tips
			}
			if(statusColumnName){
				crossColumnName.value = statusColumnName
			}
			if(statusColumnValue){
				crossColumnValue.value = statusColumnValue
			}
		}
		context?.$http({
			url: `${context?.$toolUtil.storageGet('frontSessionTable')}/session`,
			method: 'get'
		}).then(res => {
			var json = res.data.data
            if (localStorage.getItem('autoSave')) {
                localStorage.removeItem('autoSave')
                save()
            }
		})
		context?.$http({
			url: `option/shicaileixing/shicaileixing`,
			method: 'get'
		}).then(res=>{
			shicaileixingLists.value = res.data.data
		})
	}
	//初始化
	//取消
	const backClick = () => {
		history.back()
	}
	//富文本数据回调
	const editorChange = (e,name) =>{
		form.value[name] = e
	}
	//提交
	const save=()=>{
		if(form.value.shicaitupian!=null) {
			form.value.shicaitupian = form.value.shicaitupian.replace(new RegExp(context?.$config.url,"g"),"");
		}
		var table = crossTable.value
		var objcross = JSON.parse(JSON.stringify(crossRow.value))
		let crossUserId = ''
		let crossRefId = ''
		let crossOptNum = ''
		if(type.value == 'cross'){
			if(crossColumnName.value!=''){
				if(!crossColumnName.value.startsWith('[')){
					for(let o in objcross){
						if(o == crossColumnName.value){
							objcross[o] = crossColumnValue.value
						}
					}
					//修改跨表数据
					changeCrossData(objcross)
				}else{
					crossUserId = context?.$toolUtil.storageGet('userid')
					crossRefId = objcross['id']
					crossOptNum = crossColumnName.value.replace(/\[/,"").replace(/\]/,"")
				}
			}
		}
		formRef.value.validate((valid)=>{
			if(valid){
				if(crossUserId&&crossRefId){
					form.value.crossuserid = crossUserId
					form.value.crossrefid = crossRefId
					let params = {
						page: 1,
						limit: 1000,
						crossuserid:form.value.crossuserid,
						crossrefid:form.value.crossrefid,
					}
					context?.$http({
						url: `${tableName}/page`,
						method: 'get',
						params: params
					}).then(res=>{
						if(res.data.data.total>=crossOptNum){
							context?.$toolUtil.message(`${crossTips.value}`,'error')
							return false
						}else{
							context?.$http({
								url: `${tableName}/${!form.value.id ? "save" : "update"}`,
								method: 'post',
								data: form.value
							}).then(res=>{
                                context?.$toolUtil.message(`操作成功`,'success')
                                history.back()
							})
						}
					})
				}else{
					context?.$http({
						url: `${tableName}/${!form.value.id ? "save" : "update"}`,
						method: 'post',
						data: form.value
					}).then(res=>{
                        context?.$toolUtil.message(`操作成功`,'success')
                        history.back()
					})
				}
			}
		})
	}
	//修改跨表数据
	const changeCrossData=(row)=>{
        if(type.value == 'cross'){
            context?.$http({
                url: `${crossTable.value}/update`,
                method: 'post',
                data: row
            }).then(res=>{})
        }
	}
	onMounted(()=>{
		type.value = route.query.type?route.query.type:'add'
		let row = null
		let table = null
		let statusColumnName = null
		let tips = null
		let statusColumnValue = null
		if(type.value == 'cross'){
			row = context?.$toolUtil.storageGet('crossObj')?JSON.parse(context?.$toolUtil.storageGet('crossObj')):{}
			table = context?.$toolUtil.storageGet('crossTable')
			statusColumnName = context?.$toolUtil.storageGet('crossStatusColumnName')
			tips = context?.$toolUtil.storageGet('crossTips')
			statusColumnValue = context?.$toolUtil.storageGet('crossStatusColumnValue')
		}
		init(route.query.id?route.query.id:null, type.value,'', row, table, statusColumnName, tips, statusColumnValue)
	})
    onUnmounted(()=>{
        Object.keys(localStorage).map(item=>{
            if(item.startsWith('cross')){
                localStorage.removeItem(item)
            }
        })
    })
</script>
<style lang="scss" scoped>
	// 面包屑盒子
	.bread_view {
		:deep(.breadcrumb) {
			.el-breadcrumb__separator {
			}
			.first_breadcrumb {
				.el-breadcrumb__inner {
				}
			}
			.second_breadcrumb {
				.el-breadcrumb__inner {
				}
			}
		}
	}
	// 表单
	.add_form{
		// form item
		:deep(.el-form-item) {
			//label
			.el-form-item__label {
			}
			// 内容盒子
			.el-form-item__content {
				// 输入框
				.list_inp {

				}
				// 下拉框
				.list_sel {
					//去掉默认样式
					.select-trigger{
						height: 100%;
						.el-input{
							height: 100%;
						}
					}
				}
				// 富文本
				.list_editor {
				}
				//图片上传样式
				.el-upload-list  {
					//提示语
					.el-upload__tip {
					}
					//外部盒子
					.el-upload--picture-card {
						//图标
						.el-icon{
						}
					}
					.el-upload-list__item {
					}
				}
			}
		}
	}
	// 按钮盒子
	.formModel_btn_box {
		.formModel_cancel {
		}
		.formModel_cancel:hover {
		}

		.formModel_confirm {
		}
		.formModel_confirm:hover {
		}
	}
</style>
<style>
.edit_view {
    width: 1200px;
    margin: 20px auto;
    padding: 20px 20px 20px ;
    background: #fff;
    overflow: hidden;
    border: 0px solid #eee;
    font-size: 16px;
    color:#666;
}
.edit_view .add_form{
    width: 100%;
    padding: 30px;
    border:0px solid #eee
}
.edit_view .add_form .el-form-item{
    margin: 0px 0px 20px;
    display: flex;
}
.edit_view .add_form .el-form-item .el-form-item__label{
    width: 150px;
    background: none;
    text-align: right;
    display: block;
    font-size: 16px;
    color: rgb(51, 51, 51);
    font-weight: 500;
}
.edit_view .add_form .el-form-item .el-form-item__content{
    display: flex;
    justify-content: flex-start;
    align-items: center;
    flex-wrap: wrap;
    width: calc(100% - 120px);
}
.edit_view .add_form .el-form-item .el-form-item__content .list_inp{
    height: 36px;
    line-height: 36px;
    border: 1px solid rgb(221, 221, 221);
    padding: 0px 10px;
    width: 100%;
    box-sizing: border-box;
    background: rgb(255, 255, 255);
    font-size: 16px;
}

.edit_view .add_form .el-form-item .el-form-item__content .list_sel{
    line-height: 36px;
    border: 1px solid rgb(221, 221, 221);
    box-sizing: border-box;
    width: calc(100% - 0px);
    padding: 0px 10px;
    border-radius: 0px;
    background: rgb(255, 255, 255);
    font-size: 16px;
}







.edit_view .add_form .el-form-item .el-form-item__content .el-upload--picture-card{
    background-color: rgb(255, 255, 255);
    width: 100px;
    height: 90px;
    line-height: 100px;
    text-align: center;
    border: 1px solid rgb(221, 221, 221);
    border-radius: 0px;
    cursor: pointer;
}

.edit_view .add_form .el-form-item .el-form-item__content .el-upload--picture-card .el-icon{
    font-size: 32px;
    color: #999;
}
.edit_view .add_form .el-form-item .el-form-item__content .el-upload--picture-card .el-icon svg{
    color: #999;
}


.edit_view .add_form .el-form-item .el-form-item__content .img-uploader .el-upload__tip{
    font-size: 15px;
    color: #666;
    margin: 0;
}


.edit_view .add_form .el-form-item .el-form-item__content .list_editor{
    width: 100%;
    height: auto;
    margin: 0px;
    padding: 0px;
    border-radius: 0px;
    background: rgb(255, 255, 255);
    border: 1px solid rgb(221, 221, 221);
    min-height: 300px;
}

</style>
