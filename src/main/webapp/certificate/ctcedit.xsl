<?xml version="1.0"  encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code !=0 ">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					系统中目前没有发现您要的数据。
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:call-template name="outputHTML" />
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

	<xsl:template name="outputHTML">
		<div>证照种类与品类对应关系维护</div>
		<xsl:element name="table">
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">98%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:attribute name="class">ctcedit</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					证照种类
				</xsl:element>
				<xsl:element name="th">
					必备
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/list/row">
				<xsl:sort select="ctid" order="ascending" data-type="number" />
				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:attribute name="width">230</xsl:attribute>
						<xsl:element name="input">
							<xsl:attribute name="id">ctid_<xsl:value-of
								select="ctid" /></xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of
								select="ctid" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="ctid"><xsl:value-of
								select="ctid" /></xsl:attribute>
							<xsl:attribute name="onclick">saveCTC(this)</xsl:attribute>
							<xsl:if test="flag!=''">
								<xsl:attribute name="checked">true</xsl:attribute>
							</xsl:if>
						</xsl:element>
						<xsl:value-of select="ctname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="input">
							<xsl:attribute name="id">flag_<xsl:value-of
								select="ctid" /></xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of
								select="ctid" /></xsl:attribute>
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="ctid"><xsl:value-of
								select="ctid" /></xsl:attribute>
							<xsl:attribute name="onclick">saveCTC(this)</xsl:attribute>
							<xsl:if test="flag=0">
								<xsl:attribute name="checked">true</xsl:attribute>
							</xsl:if>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


