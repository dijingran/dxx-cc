package web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ���ʵ��servletPath Ϊ/ad/a12011-11-12x RequestMapping�����ӳ�����Ϊ
 * 
 * RequestMapping("/ad/a(\\w)(.*)x")<br/> ��ʱPathVar.value=1����
 * ����ֵӦ�ð�(.*)��ֵ2011-11-12 (��value�Ǵ�0��ʼ��)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVar {

	/** ��ӦrequestMapping ������ʽ�еĵڼ���group,Ҳ���ǵڼ��������е����� */
	int value() default 0;

}