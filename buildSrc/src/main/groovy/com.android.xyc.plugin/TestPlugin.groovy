package com.android.xyc.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class TestPlugin implements Plugin<Project>{
    Project project

    @Override
    void apply(Project project) {
        // 实现一个名称为testPlugin的task，设置分组为 myPlugin，并设置描述信息
//        project.task('testPlugin', group: "MyPlugin", description: "This is my first plugin")  {
//            println "## This is my first gradle plugin in testPlugin task"
//        }
        println "** This is my first gradle plugin"

        this.project=project
        //获取app插件中的变体，这里需要考虑library,还是app目录
        //如果是library目录，需要获取libraryVariants
        //不是重点，所以略过直接使用application的变体
        def variants = project.android.applicationVariants
        //在脚本分析完成之后执行
        project.afterEvaluate {
            //遍历变体
            variants.all { variant ->
                //获取java源文件目录
                def javaDirector = variant.sourceSets.get(0).getJavaDirectories().getAt(0)
                println "java源文件目录$javaDirector"

                //获取activity文件的绝对路径
                //我直接获取的activity包下的文件
                //这里和我之前说的，需要去遍历所有文件找到需要处理注解的文件，再进行处理
                //这里为了简单起见我就直接获取activity包下的文件了
                def absolutePackageDir="$javaDirector/${variant.applicationId.replaceAll('\\.','/')}/activity"

                println '创建任务'
                //创建一个生成任务

                Task compile = project.tasks.create("generate${variant.name}",GeneratorTask)
                //为任务创建一个输入属性
                compile.inputs.property'package',variant.applicationId
                //为任务创建一个输出文件 这里是app\build\generated\atp\debug\com\example\ty\gplugin\activity
                compile.outputs.file("${project.buildDir}/generated/atp/$variant.name/${variant.applicationId.replaceAll('\\.','\\\\')}\\activity")
                println "java源文件目录 real $absolutePackageDir"
                //遍历目录activity的所有文件，将文件加入任务的输入文件
                def activityPackage=new File(absolutePackageDir)
                activityPackage.eachFile {
                    compile.inputs.file(it)
                }

            }
            registerTask()
        }
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
