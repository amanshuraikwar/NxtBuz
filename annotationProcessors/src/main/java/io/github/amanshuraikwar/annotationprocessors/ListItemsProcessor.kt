package io.github.amanshuraikwar.annotationprocessors

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import io.github.amanshuraikwar.annotations.ListItem
import java.io.File
import java.lang.RuntimeException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Suppress("unused")
@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(ListItemsProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedAnnotationTypes("*")
class ListItemsProcessor: AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    class InvalidAnnotationException(msg: String): RuntimeException(msg)

    @Throws(InvalidAnnotationException::class)
    fun getListItems(roundEnv: RoundEnvironment): List<TypeElement> {
        return roundEnv.getElementsAnnotatedWith(ListItem::class.java).map { classElement ->

            if (classElement.kind != ElementKind.CLASS) {
                throw InvalidAnnotationException(
                    "Can only be applied to classes,  element: $classElement")
            }

            (classElement as TypeElement)
                .interfaces
                .find {
                    it.toString() == "io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem"
                }
                ?:
                run {
                    throw InvalidAnnotationException(
                        "Can only be applied to classes which implement RecyclerViewListItem element: $classElement")
                }

            processingEnv.messager.printMessage(
                Diagnostic.Kind.NOTE,
                classElement.toString()
            )

            @Suppress("USELESS_CAST")
            classElement as TypeElement
        }
    }

    override fun process(p0: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {

        try {

            // collecting list items
            val listItems: List<TypeElement> = getListItems(roundEnv)

            val generatedSourcesRoot: String =
                processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty()
            if(generatedSourcesRoot.isEmpty()) {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Can't find the target directory for generated Kotlin files."
                )
                return false
            }

            if (listItems.isEmpty()) {
                return false
            }

            val packageOfMethod =
                processingEnv.elementUtils.getPackageOf(listItems[0]).toString()

            val file = File(generatedSourcesRoot).apply { mkdir() }

            val fileBuilder =
                FileSpec.builder(packageOfMethod, "RecyclerViewTypeFactoryGenerated")

            // generating a view holder for each list item
            listItems.forEach { typeElement ->
                fileBuilder.addType(generateViewHolder(typeElement))
            }

            fileBuilder.addType(generateTypeFactory(listItems))

            fileBuilder.build().writeTo(file)

            return true

        } catch (e: InvalidAnnotationException) {
            e.printStackTrace()
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                e.message
            )
            return false
        }
    }

    private fun generateTypeFactory(listItems: List<TypeElement>): TypeSpec {

        val typeFactoryClassBuilder =
            TypeSpec
                .classBuilder("RecyclerViewTypeFactoryGenerated")
                .addModifiers(KModifier.PUBLIC)
                .addSuperinterface(
                    ClassName.bestGuess(
                        "io.github.amanshuraikwar.multiitemadapter.RecyclerViewTypeFactory"
                    )
                )

        typeFactoryClassBuilder.addFunction(generateGetLayout(listItems))
        typeFactoryClassBuilder.addFunction(generateCreateViewHolder(listItems))
        typeFactoryClassBuilder.addFunction(generateType(listItems))

        return typeFactoryClassBuilder.build()
    }

    private fun generateType(listItems: List<TypeElement>): FunSpec {

        val typeFunBuilder =
            FunSpec
                .builder("type")
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .addParameter("listItem", ClassName.bestGuess("io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem"))

        listItems.forEachIndexed { index, typeElement ->
            typeFunBuilder.addStatement(
                if (index == 0)
                    "if (listItem is ${typeElement.asClassName().simpleName}) return $index"
                else
                    "else if (listItem is ${typeElement.asClassName().simpleName}) return $index"
            )
        }

        typeFunBuilder
            .addStatement("else return -1")
            .returns(INT)

        return typeFunBuilder.build()
    }

    private fun generateCreateViewHolder(listItems: List<TypeElement>): FunSpec {

        val createViewHolderFunBuilder =
            FunSpec
                .builder("createViewHolder")
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .addParameter("parent", ClassName.bestGuess("android.view.View"))
                .addParameter("viewType", INT)
                .addStatement("val viewHolder: RecyclerView.ViewHolder? = null")

        listItems.forEachIndexed { index, typeElement ->
            createViewHolderFunBuilder.addStatement(
                if (index == 0)
                    "if (viewType == $index) return ${typeElement.asClassName().simpleName}ViewHolder(parent)"
                else
                    "else if (viewType == $index) return ${typeElement.asClassName().simpleName}ViewHolder(parent)"
            )
        }

        createViewHolderFunBuilder
            .addStatement("return viewHolder")
            .returns(
                ClassName
                    .bestGuess(
                        "androidx.recyclerview.widget.RecyclerView.ViewHolder"
                    )
                    .copy(true)
            )

        return createViewHolderFunBuilder.build()
    }

    private fun generateGetLayout(listItems: List<TypeElement>): FunSpec {

        val getLayoutFunBuilder =
            FunSpec
                .builder("getLayout")
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .addParameter("viewType", Int::class.java)


        listItems.forEachIndexed { index, typeElement ->
            getLayoutFunBuilder.addStatement(
                if (index == 0)
                    "if (viewType == $index) return ${typeElement.asClassName().simpleName}ViewHolder.LAYOUT"
                else
                    "else if (viewType == $index) return ${typeElement.asClassName().simpleName}ViewHolder.LAYOUT"
            )
        }

        getLayoutFunBuilder
            .addStatement("else return 0")
            .returns(INT)

        return getLayoutFunBuilder.build()
    }

    private fun generateViewHolder(
        variable: TypeElement
    ): TypeSpec {

        val classBuilder =
            TypeSpec
                .classBuilder(variable.asClassName().simpleName + "ViewHolder")
                .addModifiers(KModifier.PUBLIC)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter(
                            "view",
                            ClassName.bestGuess("android.view.View")
                        )
                        .build()
                )
                .superclass(
                    ClassName
                        .bestGuess(
                            "androidx.recyclerview.widget.RecyclerView.ViewHolder"
                        )
                )
                .addSuperclassConstructorParameter(CodeBlock.builder().add("view").build())
                .addType(
                    TypeSpec
                        .companionObjectBuilder()
                        .addProperty(
                            PropertySpec
                                .builder("LAYOUT", Int::class)
                                .mutable(false)
                                .addAnnotation(
                                    ClassName.bestGuess("androidx.annotation.LayoutRes")
                                )
                                .addAnnotation(
                                    ClassName.bestGuess("kotlin.jvm.JvmStatic")
                                )
                                .initializer(
                                    "${variable.getAnnotation(ListItem::class.java).layoutResId}",
                                    Int::class.java
                                )
                                .build()
                        )
                        .build()
                )

        return classBuilder.build()
    }
}