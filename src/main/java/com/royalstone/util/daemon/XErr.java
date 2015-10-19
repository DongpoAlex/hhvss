/*
 * Created on 2004-10-21
 *
 */
package com.royalstone.util.daemon;

import org.jdom.Element;

import com.royalstone.util.SolarCalendar;

/**	为了把查询过程中的错误信息传递给客户端, Servlet 返回的XML文档中应该包括一个xerr节点.	
 * XErr 用于生成XML文档中的xerr 节点.
 * 按照SQL中的约定,错误代码为0表示执行正常, 100表示查询正常但结果集为空, 如果查询过程中出错则以负数表示.
 * @author Mengluoyi
 *
 */
public class XErr
{
    /**
     * @param code	错误代码
     * @param note	文字说明
     */
    public XErr( int code, String note )
    {
        this.code = code;
        this.note  = note;
    }
    
    /**
     * @return	包含错误信息的XML节点.
     */
    public Element toElement()
    {
        Element elm = new Element( "xerr" );
        elm.addContent( new Element( "code" ).addContent( "" + code ));
        elm.addContent( new Element( "note" ).addContent( note ) );
        elm.addContent( new Element( "time" ).addContent( cal.toString() ) );
               return elm;
    }
    
    /**
     * <code>code</code>错误代码(缺省值为0)
     */
    final public int code;
    /**
     * <code>note</code>对执行结果的文字说明
     */
    final private String note;
    
    final private SolarCalendar cal= new SolarCalendar();
}
