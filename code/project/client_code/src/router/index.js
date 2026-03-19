import { createRouter, createWebHashHistory } from 'vue-router'
import index from '../views'
import home from '../views/pages/home.vue'
import login from '../views/pages/login.vue'
import yonghuList from '@/views/pages/yonghu/list'
import yonghuDetail from '@/views/pages/yonghu/formModel'
import yonghuAdd from '@/views/pages/yonghu/formAdd'
import yonghuRegister from '@/views/pages/yonghu/register'
import yonghuCenter from '@/views/pages/yonghu/center'
import shicaileixingList from '@/views/pages/shicaileixing/list'
import shicaileixingDetail from '@/views/pages/shicaileixing/formModel'
import shicaileixingAdd from '@/views/pages/shicaileixing/formAdd'
import storeupList from '@/views/pages/storeup/list'
import shipufenleiList from '@/views/pages/shipufenlei/list'
import shipufenleiDetail from '@/views/pages/shipufenlei/formModel'
import shipufenleiAdd from '@/views/pages/shipufenlei/formAdd'
import shipuxinxiList from '@/views/pages/shipuxinxi/list'
import shipuxinxiDetail from '@/views/pages/shipuxinxi/formModel'
import shipuxinxiAdd from '@/views/pages/shipuxinxi/formAdd'
import forumList from '@/views/pages/forum/list'
import newsList from '@/views/pages/news/list'
import shicaixinxiList from '@/views/pages/shicaixinxi/list'
import shicaixinxiDetail from '@/views/pages/shicaixinxi/formModel'
import shicaixinxiAdd from '@/views/pages/shicaixinxi/formAdd'
import shipuanpaiList from '@/views/pages/shipuanpai/list'
import shipuanpaiDetail from '@/views/pages/shipuanpai/formModel'
import shipuanpaiAdd from '@/views/pages/shipuanpai/formAdd'

const routes = [{
		path: '/',
		redirect: '/index/home'
	},
	{
		path: '/index',
		component: index,
		children: [{
			path: 'home',
			component: home
		}
		, {
			path: 'yonghuList',
			component: yonghuList
		}, {
			path: 'yonghuDetail',
			component: yonghuDetail
		}, {
			path: 'yonghuAdd',
			component: yonghuAdd
		}
		, {
			path: 'yonghuCenter',
			component: yonghuCenter
		}
		, {
			path: 'shicaileixingList',
			component: shicaileixingList
		}, {
			path: 'shicaileixingDetail',
			component: shicaileixingDetail
		}, {
			path: 'shicaileixingAdd',
			component: shicaileixingAdd
		}
        , {
            path: 'storeupList',
            component: storeupList
        }
		, {
			path: 'shipufenleiList',
			component: shipufenleiList
		}, {
			path: 'shipufenleiDetail',
			component: shipufenleiDetail
		}, {
			path: 'shipufenleiAdd',
			component: shipufenleiAdd
		}
		, {
			path: 'shipuxinxiList',
			component: shipuxinxiList
		}, {
			path: 'shipuxinxiDetail',
			component: shipuxinxiDetail
		}, {
			path: 'shipuxinxiAdd',
			component: shipuxinxiAdd
		}
		, {
			path: 'forumList',
			component: forumList
		}
		, {
			path: 'newsList',
			component: newsList
		}
		, {
			path: 'shicaixinxiList',
			component: shicaixinxiList
		}, {
			path: 'shicaixinxiDetail',
			component: shicaixinxiDetail
		}, {
			path: 'shicaixinxiAdd',
			component: shicaixinxiAdd
		}
		, {
			path: 'shipuanpaiList',
			component: shipuanpaiList
		}, {
			path: 'shipuanpaiDetail',
			component: shipuanpaiDetail
		}, {
			path: 'shipuanpaiAdd',
			component: shipuanpaiAdd
		}
		]
	},
	{
		path: '/login',
		component: login
	}
	,{
		path: '/yonghuRegister',
		component: yonghuRegister
	}
]

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

export default router
