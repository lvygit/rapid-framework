/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.org.rapid_framework.cache.aop.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import cn.org.rapid_framework.cache.aop.advice.AnnotationMethodCacheAdvice;
import cn.org.rapid_framework.cache.aop.interceptor.AnnotationMethodCacheInterceptor;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser}
 * implementation that allows users to easily configure all the infrastructure
 * beans required to enable annotation-driven transaction demarcation.
 *
 * <p>By default, all proxies are created as JDK proxies. This may cause some
 * problems if you are injecting objects as concrete classes rather than
 * interfaces. To overcome this restriction you can set the
 * '<code>proxy-target-class</code>' attribute to '<code>true</code>', which
 * will result in class-based proxies being created.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 */
class AnnotationDrivenBeanDefinitionParser  implements BeanDefinitionParser {

	/**
	 * The bean name of the internally managed transaction advisor (mode="proxy").
	 */
//	public static final String METHOD_CACHE_INTECEPTOR_BEAN_NAME =
//			"cn.org.rapid_framework.cache.aop.config.internalMethodCacheInteceptor";

	/**
	 * Parses the '<code>&lt;method-cache:annotation-driven/&gt;</code>' tag. Will
	 * {@link AopNamespaceUtils#registerAutoProxyCreatorIfNecessary register an AutoProxyCreator}
	 * with the container as necessary.
	 */
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
		return null;
	}
	
//	@Override
//	protected void doParse(Element element, BeanDefinitionBuilder builder) {
//		AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
//		return;
//	}
	
//	@Override
//	protected Class getBeanClass(Element element) {
//		return AnnotationMethodCacheInterceptor.class;
//	}

	private static void registerMethodCache(Element element, BeanDefinition def) {
		def.getPropertyValues().add("methodCacheBeanName",
				MethodCacheNamespaceHandler.getMethodCacheName(element));
	}

	/**
	 * Inner class to just introduce an AOP framework dependency when actually in proxy mode.
	 */
	private static class AopAutoProxyConfigurer {

		public static void configureAutoProxyCreator(Element element, ParserContext parserContext) {
			AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);

//			if (!parserContext.getRegistry().containsBeanDefinition(METHOD_CACHE_INTECEPTOR_BEAN_NAME)) {
				Object eleSource = parserContext.extractSource(element);

				// Create the AnnotationMethodCacheInterceptor definition.
				RootBeanDefinition interceptorDef = createAnnotationMethodCacheInterceptorDefinition(element, eleSource);
				String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

//				CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
//				compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
//				parserContext.registerComponent(compositeDef);
				
				RootBeanDefinition adviceDef = createAnnotationMethodCacheAdviceDefinition(element,eleSource,interceptorName);
				String adviceName = parserContext.getReaderContext().registerWithGeneratedName(adviceDef);
				parserContext.registerComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
				parserContext.registerComponent(new BeanComponentDefinition(adviceDef, adviceName));
				
				
//			}
				
//				BeanDefinition beanDef =
//					parserContext.getRegistry().getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
//				beanDef.getPropertyValues().add("interceptorNames", interceptorName);
//				AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext,interceptorName);
		}

		private static RootBeanDefinition createAnnotationMethodCacheInterceptorDefinition(
				Element element, Object eleSource) {
			RootBeanDefinition interceptorDef = new RootBeanDefinition(AnnotationMethodCacheInterceptor.class);
			interceptorDef.setSource(eleSource);
			interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			interceptorDef.getPropertyValues().add("methodCacheBeanName",MethodCacheNamespaceHandler.getMethodCacheName(element));
			return interceptorDef;
		}

		private static RootBeanDefinition createAnnotationMethodCacheAdviceDefinition(
				Element element, Object eleSource,String annotationMethodCacheInterceptorBeanName) {
			RootBeanDefinition def = new RootBeanDefinition(AnnotationMethodCacheAdvice.class);
			def.setSource(eleSource);
			def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			def.getPropertyValues().add("annotationMethodCacheInterceptorBeanName",annotationMethodCacheInterceptorBeanName);
			return def;
		}
		
		
	}

}
