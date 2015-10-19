<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:for-each select="/xdoc/xout">
			<xsl:call-template name="output_sheet" />
		</xsl:for-each>
	</xsl:template>


	<xsl:template name="output_sheet">
		<xsl:for-each select="list">
			<xsl:call-template name="output_list" />
		</xsl:for-each>
	</xsl:template>



	<xsl:template name="output_list">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>状态</th>
				<th>节假名称</th>
				<th>开始日期</th>
				<th>结束日期</th>
				<th>允许编辑起止日期</th>
				<th>备注</th>
				<th>最后刷新时间</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="status" />
					</td>
					<td>
						<xsl:value-of select="holiday" />
					</td>
					<td>
						<xsl:value-of select="startdate" />
					</td>
					<td>
						<xsl:value-of select="enddate" />
					</td>
					<td>
						<xsl:value-of select="sedate" />
						-
						<xsl:value-of select="eedate" />
					</td>
					<td>
						<xsl:value-of select="note" />
					</td>
					<td>
						<xsl:value-of select="updatetime" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>