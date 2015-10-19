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
		<table border="1" width="100%" id="butable" style="display:none;">
			<caption>用户BUID（所属区域）情况</caption>
			<tr>
				<th width="20%">操作</th>
				<th width="30%">BU信息</th>
				<th width="50%">备注</th>
			</tr>
			<xsl:for-each select="/xdoc/rowset/row">
				<tr>
					<td>
						<input value="修改" type="button">
							<xsl:attribute name="id"><xsl:value-of
								select="buid" /></xsl:attribute>
							<xsl:attribute name="onclick">updateBUID(this)</xsl:attribute>
						</input>
						<input value="删除" type="button">
							<xsl:attribute name="id"><xsl:value-of
								select="buid" /></xsl:attribute>
							<xsl:attribute name="onclick">delBUID(this)</xsl:attribute>
						</input>
					</td>
					<td>
						<input type="text" disabled="disabled" size="6">
							<xsl:attribute name="value"><xsl:value-of
								select="buid" /></xsl:attribute>
							<xsl:attribute name="oldValue"><xsl:value-of
								select="buid" /></xsl:attribute>
							<xsl:attribute name="onchange">showBUName(this)</xsl:attribute>
							<xsl:attribute name="id">txt_<xsl:value-of
								select="buid" /></xsl:attribute>
						</input>
					</td>
					<td>
						<xsl:attribute name="id">note_<xsl:value-of
							select="buid" /></xsl:attribute>
						<xsl:value-of select="buname" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>
					<input value="添加" type="button">
						<xsl:attribute name="onclick">addBUID(this)</xsl:attribute>
					</input>
				</td>
				<td>
					<input type="text" size="6" id="newbuid" onchange="showBUName(this)"></input>
				</td>
				<td id="noteNewbuid"></td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
