import Index from './Index';
import ErrorFragment from './ErrorFragment';
import ErrorDetailFragment from './ErrorDetailFragment';
 
import HomeActivity from './HomeActivity';
import TestActivity from './TestActivity';
import ModelListActivity from './ModelListActivity';
//start - imports
<#assign x = 0 >
<#list activities as activity>
import ${activity} from './${activity}';
<#assign x = x + 1 >
</#list>
//end - imports
export const fragmentMapper : any = {
  'layout/error.xml': ErrorFragment,
  'layout/error_detail.xml': ErrorDetailFragment,
  'layout/index.xml': HomeActivity,
  'layout/relativelayout.xml': TestActivity,
  'layout/linearlayout.xml': TestActivity,
  'layout/framelayout.xml': TestActivity,
  'layout/imageview.xml': TestActivity,
  'layout/button.xml': TestActivity,
  'layout/textview.xml': TestActivity,
  'layout/textbox.xml': TestActivity,
  'layout/gridlayout.xml': TestActivity,
  'layout/constraint_layout.xml': TestActivity,
  'layout/modeltest_listview.xml': ModelListActivity,
   //start - body 
	<#assign x = 0 >
	<#list activities as activity>
	'layout/${layoutFiles[x]}': ${activity},
	<#assign x = x + 1 >
	</#list>   
   //end - body
};