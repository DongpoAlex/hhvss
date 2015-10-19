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
				<xsl:call-template name="output_module" />
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


	<xsl:template name="output_module">
		<xsl:param name="i">
			1
		</xsl:param>


		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">cols</xsl:attribute>
			<xsl:element name="caption">
				可选择的视图定义
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					选中
				</xsl:element>
				<xsl:element name="th">
					视图编码
				</xsl:element>
				<xsl:element name="th">
					视图说明
				</xsl:element>
				<xsl:element name="th">
					所属标题
				</xsl:element>
				<xsl:element name="th">
					视图页脚
				</xsl:element>
				<xsl:element name="th">
					预览
				</xsl:element>
				<xsl:element name="th">
					维护
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/cmdefinition/row">

				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:element name="input">
							<xsl:attribute name="type">radio</xsl:attribute>
							<xsl:attribute name="name">selectcmid</xsl:attribute>
							<xsl:attribute name="value">
						<xsl:value-of select="cmid" />
					</xsl:attribute>
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="cmid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="note" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="title" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="footer" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="target">_blank</xsl:attribute>
							<xsl:attribute name="href">
				   ../cm/cmpreview.jsp?cmid=<xsl:value-of select="cmid" />
				</xsl:attribute>
							预览
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="target">_blank</xsl:attribute>
							<xsl:attribute name="href">
				   ../cm/colModleEditor.jsp?cmid=<xsl:value-of select="cmid" />
				</xsl:attribute>
							维护
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


