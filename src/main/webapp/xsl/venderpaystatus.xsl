<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<xsl:call-template name="show_list" />
	</xsl:template>

	<xsl:template name="show_list">
		<div>注意，以下结算主体已冻结！</div>
		<table border="1" width="90%" cellSpacing="1" cellspadding="3">
			<tr>
				<th>供应商编码</th>
				<th>供应商名称</th>
				<th>结算主体名称</th>
				<th>状态</th>
			</tr>
			<xsl:for-each select="/xdoc/xout/rowset/row">
				<tr>
					<td>
						<xsl:value-of select="venderid" />
					</td>
					<td>
						<xsl:value-of select="vendername" />
					</td>
					<td>
						<xsl:value-of select="payshopname" />
					</td>
					<td>
						<xsl:value-of select="payflagname" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
		<br></br>
		<div>其它结算主体可以正常结算</div>
	</xsl:template>
</xsl:stylesheet>
