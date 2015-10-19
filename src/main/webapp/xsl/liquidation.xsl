<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:for-each select="/xdoc/xout/sheet">
			<xsl:call-template name="output_sheet" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="output_sheet">
		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>
		<br />
		<xsl:for-each select="body">
			<xsl:call-template name="output_detail1" />
		</xsl:for-each>

	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td class="tdHead">单据编号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td class="tdHead">结算主体</td>
				<td>
					<xsl:value-of select="payshopid" />
					<xsl:value-of select="payshopname" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">供应商编码</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td class="tdHead">供应商名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">课类</td>
				<td>
					<xsl:value-of select="majorid" />
					<xsl:value-of select="categoryname" />
				</td>
				<td class="tdHead">生成结算单号</td>
				<td>
					<xsl:value-of select="settlesheetid" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">对账人员</td>
				<td>
					<xsl:value-of select="editor" />
				</td>
				<td class="tdHead">对账提交时间</td>
				<td>
					<xsl:value-of select="editdate" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">单据状态</td>
				<td>
					<xsl:value-of select="flagname" />
				</td>
				<td class="tdHead">备注</td>
				<td>
					<xsl:value-of select="note" />
				</td>
			</tr>
		</table>

	</xsl:template>

	<xsl:template name="output_detail1">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>序号</th>
				<th>所属BU</th>
				<th>单据编码</th>
				<th>单据类型</th>
				<th>对账金额</th>
				<th>备注</th>
			</tr>
			<xsl:for-each select="row">
				<xsl:sort select="seqno" order="ascending" data-type="number" />
				<tr>
					<td>
						<xsl:value-of select="seqno" />
					</td>
					<td>
						<xsl:value-of select="buid" />
						<xsl:value-of select="buname" />
					</td>
					<td>
						<xsl:value-of select="sheetno" />
					</td>
					<td>
						<xsl:value-of select="sheettype" />
						<xsl:value-of select="sheettypename" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(unpaidamt),'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="note" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>