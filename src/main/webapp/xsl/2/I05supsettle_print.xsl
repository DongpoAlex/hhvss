<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:template match="/">
		<xsl:for-each select="/xdoc/xout/sheet">
			<xsl:call-template name="output_sheet" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="output_sheet">
		<div id="shuiyin" style="display: none;">
			<xsl:value-of select="/xdoc/xout/sheet/head/row/flagname" />
		</div>
		<h2 align="center">供应商结算通知单</h2>
		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>
		<hr></hr>
		<xsl:for-each select="body4">
			<xsl:call-template name="output_body4" />
		</xsl:for-each>
		<hr></hr>
		<xsl:for-each select="body5">
			<xsl:call-template name="output_body5" />
		</xsl:for-each>
		<hr></hr>
		<xsl:for-each select="body6">
			<xsl:call-template name="output_body6"/>
		</xsl:for-each>
		<hr></hr>
	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="0" border="1">
			<tr class="subhead">
				<td>
					合同品类：
					<xsl:value-of select="cat" />
					<xsl:value-of select="catname" />
				</td>
				<td colspan="2" align="center">
					代销结算单
				</td>
				<td>
					结算地点
					<xsl:value-of select="mrid" />
					<xsl:value-of select="mridname" />
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
					[
					<xsl:value-of select="supid" />
					]
					<xsl:value-of select="c5" />
				</td>
				<td>单据类别</td>
				<td>
					<xsl:value-of select="btype" />
					<xsl:value-of select="btypename" />
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
				<td colspan="3">
					<xsl:value-of select="accntno" />
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
				<td rowspan="5">结算款项</td>
				<td>销售收入</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n1,'#0.00')" />
				</td>
				<td>销售调整</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n4,'#0.00')" />
				</td>
				<td>供应商折扣</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n3,'#0.00')" />
				</td>
				<td>结算金额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(totyfje,'#0.00')" />
				</td>
			</tr>
			<tr>
				<td>0.04税额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n20,'#0.00')" />
				</td>
				<td>0.06税额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n26,'#0.00')" />
				</td>
				<td>0.10税额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n27,'#0.00')" />
				</td>
				<td>损益分担</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n28,'#0.00')" />
				</td>
			</tr>
			<tr>
				<td>0.13税额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n18,'#0.00')" />
				</td>
				<td>0.17税额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n19,'#0.00')" />
				</td>
				<td>总税额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n24,'#0.00')" />
				</td>
				<td>差异调整</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n13,'#0.00')" />
				</td>
			</tr>
			<tr>
				<td>扣款合计</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(totkk,'#0.00')" />
				</td>
				<td>预付款</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(yfkje,'#0.00')" />
				</td>
				<td>本次余额J</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(thisye,'#0.00')" />
				</td>
				<td>本次余额F</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n10,'#0.00')" />
				</td>
			</tr>
			<tr>
				<td>上次余额J</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(lastye,'#0.00')" />
				</td>
				<td>上次金额F</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(n9,'#0.00')" />
				</td>
				<td>实结余额</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(sjfkje,'#0.00')" />
				</td>
				<td>付款天数天数</td>
				<td class="num-fmt">
					<xsl:value-of select="n30" />
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
			<tr>
				<td rowspan="2">备注</td>
				<td colspan="8">
					<h3>
						请按￥
						<xsl:value-of select="format-number(sum(invoicevalue),'#0.00')" />
						元开增值税发票
						大写：
						<xsl:value-of select="invoicevalueupper" />
						<br />
						<xsl:value-of select="memo" />
					</h3>
				</td>
			</tr>
			<tr>
				<td>品牌</td>
				<td colspan="2">
					<xsl:value-of select="person5" />
				</td>
				<td>停款人</td>
				<td colspan="1">
					<xsl:value-of select="person1" />
				</td>
				<td>停款原因</td>
				<td colspan="3">
					<xsl:value-of select="person3" />
				</td>
			</tr>
		</table>

	</xsl:template>


	<xsl:template name="output_body4">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<caption align="left" font-size="26px"> 附表一(单据/销售)</caption>
			<tr>
				<th>税率</th>
				<th>门店</th>
				<th>销售收入</th>
				<th>结帐含税金额</th>
				<th>结帐不含税金额</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="n11 * 100" />
						%
					</td>
					<td>
						<xsl:value-of select="mrid" />
						<xsl:value-of select="shopname" />
					</td>
					<td class="num-fmt">
						<xsl:value-of select="format-number(n9,'#0.00')" />
					</td>

					<td class="num-fmt">
						<xsl:value-of select="format-number(n15,'#0.00')" />
					</td>
					<td class="num-fmt">
						<xsl:value-of select="format-number(n16,'#0.00')" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>合计　</td>
				<td>
				</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(sum(row/n9),'#0.00')" />
				</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(sum(row/n15),'#0.00')" />
				</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(sum(row/n16),'#0.00')" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_body5">

		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<caption align="left" font-size="26px"> 附表二(费用)</caption>
			<tr>
				<th>收取机构</th>
				<th>费用课别</th>
				<th>费用代码</th>
				<th>费用名称</th>
				<th>扣项类型</th>
				<th>费用金额</th>
				<!-- <th>费用比率</th> <th>扣项基数</th> -->
				<th>备注</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="mrid" />
					</td>
					<td>
						<xsl:value-of select="maid" />
					</td>
					<td>
						<xsl:value-of select="chargeid" />
					</td>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td>
						<xsl:value-of select="chargetype" />
					</td>
					<td class="num-fmt">
						<xsl:value-of select="format-number(money,'#0.00')" />
					</td>
					<!-- <td class="num-fmt"> <xsl:value-of select="format-number(calcrate,'#0.00')" 
						/> </td> <td class="num-fmt"> <xsl:value-of select="format-number(calcbase,'#0.00')" 
						/> </td> -->
					<td>
						<xsl:value-of select="note" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>
					合计
				</td>
				<td>

				</td>
				<td>

				</td>
				<td>

				</td>
				<td>

				</td>
				<td class="num-fmt">
					<xsl:value-of select="format-number(sum(row/money),'#0.00')" />
				</td>
				<td>

				</td>
				<!-- <td class="num-fmt"> <xsl:value-of select="format-number(sum(row/calcbase),'#0.00')" 
					/> </td> -->
				<td>

				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_body6">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<caption>
				<h3>回执单</h3>
			</caption>
			<xsl:for-each select="/xdoc/xout/sheet/head/row">
				<tr center="left">
					<td>供应商:[
						<xsl:value-of select="supid"/>
						]
						<xsl:value-of select="c5"/>
					</td>
					<td>结算单号:</td>
					<td>
						<xsl:value-of select="billno"/>
					</td>
					<td>结算方式:</td>
					<td>
						<xsl:value-of select="n25"/>
					</td>
					<td>结算地点：</td>
					<td>
						<xsl:value-of select="mrid"/>
						<xsl:value-of select="mridname"/>
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<th colspan="2" >费用课别</th>
				<th>费用代码</th>
				<th colspan="2">费用名称</th>
				<th>扣项类型</th>
				<th>费用金额</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td colspan="2">
						<xsl:value-of select="maid"/>
					</td>
					<td>
						<xsl:value-of select="chargeid"/>
					</td>
					<td colspan="2">
						<xsl:value-of select="chargename"/>
					</td>
					<td>
						<xsl:value-of select="chargetype"/>
					</td>
					<td>
						<xsl:value-of select="format-number(money,'#0.00')"/>
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>合计</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td>
					<xsl:value-of select="format-number(sum(row/money),'#0.00')"/>
				</td>

			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
