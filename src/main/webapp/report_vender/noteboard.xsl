<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:call-template name="output_list" />
	</xsl:template>

	<xsl:template name="output_list">
		<xsl:element name="table">
			<xsl:attribute name="class">noteboard_list</xsl:attribute>
			<xsl:attribute name="border">0</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">4</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="width">50</xsl:attribute>
					公文号
				</xsl:element>
				<xsl:element name="th">
					公文标题
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="width">50</xsl:attribute>
					发布人
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="width">70</xsl:attribute>
					发布日期
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="width">70</xsl:attribute>
					有效日期
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="width">40</xsl:attribute>
					状态
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/catalogue/row">
				<xsl:element name="tr">
					<xsl:attribute name="onclick">
				open_notebord(<xsl:value-of select="noteid"></xsl:value-of>);
			</xsl:attribute>
					<xsl:element name="td">
						<xsl:value-of select="noteid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="number(istop)=1">
							<span style="color:red;">[置顶]</span>
						</xsl:if>
						<xsl:value-of select="title" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="editor" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="editdate" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="expiredate" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="number(call_number)>0">
							已阅读
						</xsl:if>
						<xsl:if test="number(call_number)=0">
							<span style="color:red;">未阅读</span>
						</xsl:if>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>