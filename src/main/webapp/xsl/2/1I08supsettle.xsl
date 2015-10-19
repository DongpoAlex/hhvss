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
		<div id="shuiyin">
			<xsl:value-of select="/xdoc/xout/sheet/head/row/flagname" />
		</div>
		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>

		<xsl:for-each select="body">
			<xsl:call-template name="output_body" />
		</xsl:for-each>

		<xsl:for-each select="body2">
			<xsl:call-template name="output_body2" />
		</xsl:for-each>

		<xsl:for-each select="body3">
			<xsl:call-template name="output_body3" />
		</xsl:for-each>
	</xsl:template>


	<xsl:template name="output_head">
		<center>
			<h4>
				<xsl:value-of select="c6" />
			</h4>
		</center>
		<table width="99%" cellspacing="0" cellpadding="0" border="0">
			<tr class="subhead">
				<td>
					门店 [
					<xsl:value-of select="pcid" />
					]
					<xsl:value-of select="pcidname" />
				</td>
				<td>
					<xsl:value-of select="btypename" />
				</td>
				<td>
					结算地点 [
					<xsl:value-of select="mrid" />
					]
					<xsl:value-of select="mridname" />
				</td>
				<td>
					<xsl:if test="maid!=''">
						业态：
						<xsl:value-of select="maid" />
					</xsl:if>
				</td>
			</tr>
			<tr>
				<td>
					结算单号：
					<xsl:value-of select="billno" />
				</td>
				<td>
					上次结算日期：
					<xsl:value-of select="lastdate" />
				</td>
				<td>
					本次结算日期：
					<xsl:value-of select="thisdate" />
				</td>
				<td>金额单位：元</td>
			</tr>
		</table>
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>供应商</td>
				<td colspan="4">
					<xsl:value-of select="supid" />
					<xsl:value-of select="c5" />
				</td>
				<td>单据类别</td>
				<td>
					[
					<xsl:value-of select="btype" />
					]
					<xsl:value-of select="btypename" />
					单
				</td>
				<td>审核标志</td>
				<td>
					[
					<xsl:value-of select="flag" />
					]
					<xsl:value-of select="flagname" />
				</td>
			</tr>
			<tr>
				<td>开户银行</td>
				<td colspan="4">
					<xsl:value-of select="bank" />
				</td>
				<td>账号</td>
				<td colspan="2">
					<xsl:value-of select="accntno" />
				</td>
				<td>
					<xsl:if test="maid='超市'">
						结算方式：
						<xsl:value-of select="paytype" />
					</xsl:if>
				</td>
			</tr>
			<tr>
				<td>税号</td>
				<td colspan="4">
					<xsl:value-of select="taxno" />
				</td>
				<td>合同号</td>
				<td>
					<xsl:value-of select="contno" />
				</td>
				<td>到期日期</td>
				<td>
					<xsl:value-of select="edate" />
				</td>
			</tr>
			<tr>
				<td rowspan="6">结算款项</td>
				<td>销售收入</td>
				<td>
					<xsl:value-of select="n1" />
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>供应商折扣</td>
				<td>
					<xsl:value-of select="n3" />
				</td>
				<td>结算金额</td>
				<td>
					<xsl:value-of select="totyfje" />
				</td>
			</tr>
			<tr>
				<td>0.04税额</td>
				<td>
					<xsl:value-of select="n20" />
				</td>
				<td>0.06税额</td>
				<td>
					<xsl:value-of select="n26" />
				</td>
				<td>0.10税额</td>
				<td>
					<xsl:value-of select="n27" />
				</td>
				<td>结算调整</td>
				<td>
					<xsl:value-of select="adjustje" />
				</td>
			</tr>
			<tr>
				<td>0.13税额</td>
				<td>
					<xsl:value-of select="n18" />
				</td>
				<td>0.17税额</td>
				<td>
					<xsl:value-of select="n19" />
				</td>
				<td>总税额</td>
				<td>
					<xsl:value-of select="n24" />
				</td>
				<td>差异调整</td>
				<td>
					<xsl:value-of select="n13" />
				</td>
			</tr>
			<tr>
				<td>扣款合计</td>
				<td>
					<xsl:value-of select="totkk" />
				</td>
				<td>预付款</td>
				<td>
					<xsl:value-of select="yfkje" />
				</td>
				<td>应提金额</td>
				<td>
					<xsl:value-of select="ogdje" />
				</td>
				<td>销售提成</td>
				<td>
					<xsl:value-of select="n8" />
				</td>
			</tr>
			<tr>
				<td>独立促销</td>
				<td>
					<xsl:value-of select="mgdje" />
				</td>
				<td>差额提成</td>
				<td>
					<xsl:value-of select="aqdje" />
				</td>
				<td>实际提成</td>
				<td>
					<xsl:value-of select="totdje" />
				</td>
				<td>发票调整</td>
				<td>
					<xsl:value-of select="n14" />
				</td>
			</tr>
			<tr>
				<td>上次余额</td>
				<td>
					<xsl:value-of select="lastye" />
				</td>
				<td>实结金额</td>
				<td>
					<xsl:value-of select="sjfkje" />
				</td>
				<td>本次余额</td>
				<td>
					<xsl:value-of select="thisye" />
				</td>
				<td>账期天数</td>
				<td>
					<xsl:value-of select="payday" />
				</td>
			</tr>
			<tr>
				<td>录入员</td>
				<td colspan="2">
					<xsl:value-of select="inputor" />
				</td>
				<td>审核人</td>
				<td colspan="2">
					<xsl:value-of select="auditor" />
				</td>
				<td>录入日期</td>
				<td colspan="2">
					<xsl:value-of select="inputdate" />
				</td>
			</tr>
			<tr>
				<td>门店财务负责人</td>
				<td colspan="2">
				</td>
				<td>门店经理</td>
				<td colspan="2">
				</td>
				<td>审核日期</td>
				<td colspan="2">
					<xsl:value-of select="auditdate" />
				</td>
			</tr>
			<tr>
				<td rowspan="2">财务本部</td>
				<td rowspan="2" colspan="2">
				</td>
				<td rowspan="2">采购本部</td>
				<td rowspan="2" colspan="2">
				</td>
				<td>上次结算单号</td>
				<td colspan="2">
					<xsl:value-of select="c1" />
				</td>
			</tr>
			<tr>
				<td>预收发票金额</td>
				<td colspan="2">
					<xsl:value-of select="n29" />
				</td>
			</tr>
			<tr>
				<td>公司全称</td>
				<td colspan="4">
					<xsl:value-of select="c6" />
				</td>
				<td>公司税号</td>
				<td colspan="3">
					<xsl:value-of select="c8" />
				</td>
			</tr>
			<tr>
				<td>公司地址</td>
				<td colspan="4">
					<xsl:value-of select="c7" />
				</td>
				<td>公司账号</td>
				<td colspan="3">
					<xsl:value-of select="c9" />
				</td>
			</tr>
			<tr>
				<td>公司电话</td>
				<td colspan="4">
					<xsl:value-of select="c10" />
				</td>
				<td>开户银行</td>
				<td colspan="3">
					<xsl:value-of select="c4" />
				</td>
			</tr>
			<xsl:if test="stopname=''">
				<tr>
					<td>备注</td>
					<td colspan="8">
						<h3>
							请按
							<xsl:value-of select="c2" />
							元开增值税发票
						</h3>
					</td>
				</tr>

			</xsl:if>
			<xsl:if test="stopname!=''">
				<tr>
					<td rowspan="2">备注</td>
					<td colspan="8">
						<h3>
							请按
							<xsl:value-of select="c2" />
							元开增值税发票
						</h3>
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>停款人</td>
					<td>
						<xsl:value-of select="person1" />
					</td>
					<td>停款原因</td>
					<td>
						<xsl:value-of select="stopname" />
					</td>
					<td colspan="3">
					</td>
				</tr>
			</xsl:if>
		</table>

	</xsl:template>


	<xsl:template name="output_body">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>行号</th>
				<th>税率</th>
				<th>门店</th>
				<th>含税进价金额</th>
				<th>不含税进价金额</th>
				<th>供应商折扣</th>
				<th>含税调整</th>
				<th>不含税调整</th>
				<th>结账含税金额</th>
				<th>结账不含税金额</th>
				<th>销售提成</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="rowno" />
					</td>
					<td>
						<xsl:value-of select="n11" />
					</td>
					<td>
						[
						<xsl:value-of select="mrid" />
						]
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="n1" />
					</td>
					<td>
						<xsl:value-of select="n2" />
					</td>
					<td>
						<xsl:value-of select="n8" />
					</td>
					<td>
						<xsl:value-of select="n4" />
					</td>
					<td>
						<xsl:value-of select="n5" />
					</td>
					<td>
						<xsl:value-of select="n15" />
					</td>
					<td>
						<xsl:value-of select="n16" />
					</td>
					<td>
						<xsl:value-of select="n29" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n3),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n1),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n2),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n8),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n4),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n5),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n15),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n16),'#0.00')" />
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/n29),'#0.00')" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_body2">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>行号</th>
				<th>卖场</th>
				<th>费用代码</th>
				<th>费用名称</th>
				<th>费用金额</th>
				<th>合同</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="rowno" />
					</td>
					<td>
						[
						<xsl:value-of select="mrid" />
						]
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="chargeid" />
					</td>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td>
						<xsl:value-of select="money" />
					</td>
					<td>
						<xsl:value-of select="contno" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
					<xsl:value-of select="format-number(sum(row/money),'#0.00')" />
				</td>
				<td>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_body3"></xsl:template>
</xsl:stylesheet>