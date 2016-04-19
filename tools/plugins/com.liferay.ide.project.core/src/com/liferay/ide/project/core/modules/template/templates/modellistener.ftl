<#include "./basetemplate.ftl">

<#include "./component.ftl">

public class ${classname} extends ${supperclass}<${simplemodelclass}> {

	@Override
	public void onBeforeCreate(${simplemodelclass} model)
		throws ModelListenerException {

		System.out.println("Model Class Name: " +  model.getModelClassName());

	}

}