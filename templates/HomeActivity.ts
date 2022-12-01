import {Activity, Inject} from './app/Activity';
import ListView from './view/ListView';

import * as linearlayout from './android/widget/LinearLayoutImpl';
import * as view from './android/widget/ViewImpl';
import * as textview from './android/widget/TextViewImpl';
import {Gravity} from './widget/TypeConstants';
import {plainToClass} from "class-transformer";
import {NavController, InjectController} from './navigation/NavController';
import {OnClickEvent} from "./android/widget/ViewImpl";

export default class HomeActivity extends Activity{
    @Inject({ id : "@+id/listView"})
    private listView!: ListView;

	@InjectController({})
    navController!: NavController;

    constructor() {
        super();
    }

    async onCreate(obj:any) {
        this.listView.setBorderColor("red");
        this.listView.setBorderBottomWidth("2dp");
        let data = [
                {"id": "framelayout","name" : "Frame Layout"},
                {"id": "linearlayout","name" : "Linear Layout"},
                {"id": "relativelayout","name" : "Relative Layout"},
                {"id": "tablelayout","name" : "Table Layout"},
                {"id": "gridlayout","name" : "Grid Layout"},
                {"id": "constraintlayout","name" : "Constraint Layout"},
                {"id": "imageview","name" : "Image View"},
                {"id": "button","name" : "Button"},
                {"id": "textview","name" : "TextView"},
				//start - body    
				<#assign x = 0 >
				<#list activities as activity>
				{"id": "${activity}","name" : "${activity}"},
				<#assign x = x + 1 >
				</#list>
                //end - body
        ];
        let filteredData = [];
        for (let i=0;i < data.length;i++) {
        	let name:string = data[i].name;
        	if (name.indexOf('Ios') != -1) {
        		filteredData.push(data[i]);
        	}
        }
        this.listView.updateModelData(filteredData, "layouts->view as list");
        await this.executeCommand(this.listView);
    }

     goToView = async(obj:OnClickEvent) => {
        if (obj.id == 'linearlayout') {
            await this.navController.reset().navigate("fragment#framelayout#layout/linearlayout.xml", "", {}).executeCommand();
        } else if (obj.id == 'framelayout') {
            await this.navController.reset().navigate("fragment#framelayout#layout/framelayout.xml", "", {}).executeCommand();
        } else if (obj.id == 'relativelayout') {
             await this.navController.reset().navigate("fragment#framelayout#layout/relativelayout.xml", "", {}).executeCommand();
        } else if (obj.id == 'tablelayout') {
               await this.navController.reset().navigate("fragment#framelayout#layout/tablelayout.xml", "", {}).executeCommand();
        } else if (obj.id == 'imageview') {
              await this.navController.reset().navigate("fragment#framelayout#layout/imageview.xml", "", {}).executeCommand();
        } else if (obj.id == 'button') {
             await this.navController.reset().navigate("fragment#framelayout#layout/button.xml", "", {}).executeCommand();
        } else if (obj.id == 'textview') {
            await this.navController.reset().navigate("fragment#framelayout#layout/textview.xml", "", {}).executeCommand();
        } else if (obj.id == 'textbox') {
          await this.navController.reset().navigate("fragment#framelayout#layout/edittext_test.xml", "", {}).executeCommand();
        } else if (obj.id == 'gridlayout') {
            await this.navController.reset().navigate("fragment#framelayout#layout/gridlayout.xml", "", {}).executeCommand();
        } else if (obj.id == 'constraintlayout') {
            await this.navController.reset().navigate("fragment#framelayout#layout/constraint_layout.xml", "", {}).executeCommand();
        } 
        //start - imports                
		<#assign x = 0 >
		<#list activities as activity>
		else if (obj.id == '${activity}') {
        	await this.navController.reset().navigate("fragment#framelayout#layout/${layoutFiles[x]}", "testObj->view as pathmap", {"testObj": {"emailIntent": "ram@a.com", "passwordIntent": "b.com"}, looptest: {textlayout: [{"sectionName":"test123"}, {"id":1, "a": "1"}, {"id":2, "a": "2"}]}}).executeCommand();
    	}	
		<#assign x = x + 1 >
		</#list>
    	//end - imports
    }

}
