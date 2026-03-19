<template>
	<div>
		<el-dialog modal-class="edit_form_modal" class="edit_form" v-model="formVisible" :title="formTitle" width="80%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" :rules="rules">
				<el-row >
					<el-col :span="12">
						<el-form-item label="食材名称" prop="shicaimingcheng">
							<el-input class="list_inp" v-model="form.shicaimingcheng" placeholder="食材名称"
                                type="text"
								:readonly="!isAdd||disabledForm.shicaimingcheng?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item prop="shicaitupian"
									  label="食材图片"
						>
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
                                type="text"
								:readonly="!isAdd||disabledForm.shicaigongxiao?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="保存方式" prop="baocunfangshi">
							<el-input class="list_inp" v-model="form.baocunfangshi" placeholder="保存方式"
                                type="text"
								:readonly="!isAdd||disabledForm.baocunfangshi?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="12">
						<el-form-item label="食材用途" prop="shicaiyongtu">
							<el-input class="list_inp" v-model="form.shicaiyongtu" placeholder="食材用途"
                                type="text"
								:readonly="!isAdd||disabledForm.shicaiyongtu?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="食材详情" prop="shicaixiangqing">
							<editor :value="form.shicaixiangqing" placeholder="请输入食材详情" :readonly="!isAdd||disabledForm.shicaixiangqing?true:false"
								class="list_editor" @change="(e)=>editorChange(e,'shicaixiangqing')"></editor>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer v-if="isAdd||type=='logistics'||type=='reply'">
				<span class="formModel_btn_box">
					<el-button class="cancel_btn" @click="closeClick">取消</el-button>
					<el-button class="confirm_btn" type="primary" @click="save"
						>
						提交
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		computed,
		defineEmits
	} from 'vue'
    import {
        useStore
    } from 'vuex';
    const store = useStore()
    const user = computed(()=>store.getters['user/session'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const emit = defineEmits(['formModelChange'])
	//基础信息
	const tableName = 'shicaixinxi'
	const formName = '食材信息'
	//基础信息
	//form表单
	const form = ref({})
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
	const formVisible = ref(false)
	const isAdd = ref(false)
	const formTitle = ref('')

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
	//表单验证

	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
	//食材图片上传回调
	const shicaitupianUploadSuccess=(e)=>{
		form.value.shicaitupian = e
	}
	//食材类型列表
	const shicaileixingLists = ref([])

	//获取唯一标识
	const getUUID =()=> {
      return new Date().getTime();
    }
	//重置
	const resetForm = () => {
		form.value = {
			shicaimingcheng: '',
			shicaitupian: '',
			shicaileixing: '',
			shicaigongxiao: '',
			baocunfangshi: '',
			shicaiyongtu: '',
			shicaixiangqing: '',
			clicktime: '',
		}
	}
	//获取info
	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			let reg=new RegExp('../../../file','g')
			res.data.data.shicaixiangqing = res.data.data.shicaixiangqing?(res.data.data.shicaixiangqing.replace(reg,'../../../project/file')):'';
			form.value = res.data.data
			formVisible.value = true
		})
	}
	const crossRow = ref('')
	const crossTable = ref('')
	const crossTips = ref('')
	const crossColumnName = ref('')
	const crossColumnValue = ref('')
	//初始化
	const init=(formId=null,formType='add',formNames='',row=null,table=null,statusColumnName=null,tips=null,statusColumnValue=null)=>{
		resetForm()
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'add'){
			isAdd.value = true
			formTitle.value = '新增' + formName
			formVisible.value = true
		}else if(formType == 'info'){
			isAdd.value = false
			formTitle.value = '查看' + formName
			getInfo()
		}else if(formType == 'edit'){
			isAdd.value = true
			formTitle.value = '修改' + formName
			getInfo()
		}
		else if(formType == 'cross'){
			isAdd.value = true
			formTitle.value = formNames
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
			formVisible.value = true
		}

		context?.$http({
			url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
			method: 'get'
		}).then(res => {
			var json = res.data.data
		})
		context?.$http({
			url: `option/shicaileixing/shicaileixing`,
			method: 'get'
		}).then(res=>{
			shicaileixingLists.value = res.data.data
		})
	}
	//初始化
	//声明父级调用
	defineExpose({
		init
	})
	//关闭
	const closeClick = () => {
		formVisible.value = false
	}
	//富文本
	const editorChange = (e,name) =>{
		form.value[name] = e
	}
	//提交
	const save= async ()=>{
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
					crossUserId = user.value.id
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
							}).then(async res=>{
								emit('formModelChange')
								context?.$toolUtil.message(`操作成功`,'success')
                                formVisible.value = false
							})
						}
					})
				}else{
					context?.$http({
						url: `${tableName}/${!form.value.id ? "save" : "update"}`,
						method: 'post',
						data: form.value
					}).then(async (res)=>{
						emit('formModelChange')
						context?.$toolUtil.message(`操作成功`,'success')
                        formVisible.value = false
					})
				}
			}else{
                context.$message.error('请完善信息')
            }
		})
	}
	//修改跨表数据
	const changeCrossData = async (row)=>{
        if(type.value == 'cross'){
            await context?.$http({
                url: `${crossTable.value}/update`,
                method: 'post',
                data: row
            }).then(res=>{})
        }
	}
</script>
<style lang="scss" scoped>
	// 表单
	.formModel_form{
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
		.cancel_btn {
		}
		.cancel_btn:hover {
		}

		.confirm_btn {
		}
		.confirm_btn:hover {
		}
	}
</style>
