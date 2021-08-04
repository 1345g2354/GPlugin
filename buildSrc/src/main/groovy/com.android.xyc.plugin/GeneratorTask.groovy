package com.android.xyc.plugin

import org.eclipse.jdt.core.dom.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/*
 * Created by TY on 2018/5/4.
 */

class GeneratorTask extends DefaultTask {

    GeneratorTask() {
        group = 'assisclass'
        outputs.upToDateWhen { false }
    }

    @TaskAction
    def run() {
        //创建输出目录,如果存在则删除重新创建
        def outDir = outputs.files.singleFile
        outDir.deleteDir()
        outDir.mkdirs()
        //获取传入的属性包名
        def packageId = inputs.getProperties().'package'
        //遍历对应的输入文件
        inputs.files.each {
            println 'activity目录中的文件文件' + it.name
            if (it.file && it.name.endsWith('java')) {
                println '目标文件' + it.name
                //AST分析
                def parser = ASTParser.newParser(AST.JLS3) as ASTParser //initialize
                parser.setKind(ASTParser.K_COMPILATION_UNIT)     //to parse compilation unit
                parser.setSource(it.text.toCharArray())
                //content is a string which stores the java source
                parser.setResolveBindings(true)
                CompilationUnit result = (CompilationUnit) parser.createAST(null)
                //获取类名
                List types = result.types()
                TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
                //获取成员变量
                FieldDeclaration[] fieldDec = typeDec.getFields();
                def fileCollection = [:]
                for (FieldDeclaration field : fieldDec) {
                    //这里只能拿到注解名字，不能拿到包名，所以说前面需要去判断是否有导入过注解的包
                    //这就和注解处理器的有差别了
                    System.out.println("Field fragment:" + field.fragments())
                    System.out.println("Field type:" + field.getType())
                    println field.modifiers().get(0).typeName
                    def value = field.modifiers().get(0).value
                    //找到有BindView注解的属性
                    if (field.modifiers().get(0).typeName.toString() == 'BindView') {
                        fileCollection.put(field.fragments().get(0).name, value)
                    }
                }

                //拿到注解的成员变量之后，我们就直接生成代码
                File assistFile = new File(outDir, "${typeDec.name}_ViewBinding.java")
                //拼接对应的内容
                def content = """
package ${packageId};
import  ${packageId}.R;
import  ${packageId}.activity.${typeDec.name};
public class ${typeDec.name}_ViewBinding {

    public ${typeDec.name}_ViewBinding(${typeDec.name} target) {
        ${-> getBody(fileCollection)}

    }
}
"""
                assistFile.write(content)
            }
        }


//        registerTask()
    }

    def getBody(fileCollection) {
        def sb = new StringBuilder()
        fileCollection.each {
            key, value ->
                sb.append("target.${key}=target.getWindow().getDecorView().findViewById(${value.qualifier.qualifier}.${value.qualifier.name}.${value.name});")
        }
        sb.toString()
    }

    def registerTask(){
        //注册源文件生成任务，将任务的文件以及对应的任务进行注册
        //这样打包的时候就会先将对应任务执行完成，并将生成的输出文件导包进入最后的apk
        project.android.applicationVariants.each {
            variant->
                def name="generate${variant.name}"
                def fileName =    project.tasks.getByName(name).outputs.files.files.iterator().next().toString()
                println "task 生成的文件 real $fileName"
                variant.registerJavaGeneratingTask(project.tasks.getByName(name),
                        project.tasks.getByName(name).outputs.files.files)
        }

    }
}
