<?xml version="1.0"  encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code = 100">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					<xsl:value-of select="xdoc/xerr/note" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:call-template name="output_list" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:attribute name="class">warning</xsl:attribute>
					<xsl:value-of select="xdoc/xerr/code" />
					:
					<xsl:value-of select="xdoc/xerr/note" />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="output_list">
		<table border="1" width="100%" id="bustable">
			<caption>用户业务ID（供应商编码）情况</caption>
			<tr>
				<th width="20%">操作</th>
				<th width="30%">业务ID（供应商编码）</th>
				<th width="50%"></th>
			</tr>
			<tr>
				<td colspan="3">默认业务ID</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/deflist/defid">
				<tr>
					<td>
						<input value="修改" type="button" disabled="disabled">
							<xsl:attribute name="id"><xsl:value-of
								select="@defid" /></xsl:attribute>
							<xsl:attribute name="onclick">updateDef(this)</xsl:attribute>
						</input>
					</td>
					<td>
						<input type="text" disabled="disabled" size="12">
							<xsl:attribute name="value"><xsl:value-of
								select="@defid" /></xsl:attribute>
							<xsl:attribute name="oldValue"><xsl:value-of
								select="@defid" /></xsl:attribute>
							<xsl:attribute name="onchange">showVenderName(this,'<xsl:value-of
								select="@defid" />')</xsl:attribute>
							<xsl:attribute name="id">txt_<xsl:value-of
								select="@defid" /></xsl:attribute>
						</input>
					</td>
					<td>
						<xsl:attribute name="id">note_<xsl:value-of
							select="@defid" /></xsl:attribute>
						默认业务ID不允许改动
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="3">扩展业务ID（可以切换到其它供应商编码）</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/extlist/extid">
				<tr>
					<td>
						<input value="修改" type="button">
							<xsl:attribute name="id"><xsl:value-of
								select="@extid" /></xsl:attribute>
							<xsl:attribute name="onclick">updateExt(this)</xsl:attribute>
						</input>
						<input value="删除" type="button">
							<xsl:attribute name="id"><xsl:value-of
								select="@extid" /></xsl:attribute>
							<xsl:attribute name="onclick">delExt(this)</xsl:attribute>
						</input>
					</td>
					<td>
						<input type="text" disabled="disabled" size="12">
							<xsl:attribute name="value"><xsl:value-of
								select="@extid" /></xsl:attribute>
							<xsl:attribute name="oldValue"><xsl:value-of
								select="@extid" /></xsl:attribute>
							<xsl:attribute name="onchange">showVenderName(this,'<xsl:value-of
								select="@extid" />')</xsl:attribute>
							<xsl:attribute name="id">txt_<xsl:value-of
								select="@extid" /></xsl:attribute>
						</input>
					</td>
					<td>
						<xsl:attribute name="id">note_<xsl:value-of
							select="@extid" /></xsl:attribute>
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>
					<input value="添加" type="button">
						<xsl:attribute name="onclick">addExt(this)</xsl:attribute>
					</input>
				</td>
				<td>
					<input type="text" size="6" id="newVenderid" onchange="checkVenderid(this)"></input>
				</td>
				<td id="noteNewVenderid"></td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
