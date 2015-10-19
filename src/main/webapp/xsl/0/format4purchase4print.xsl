<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when
				test="/xdoc/xout/sheet/@hasRetnotice and /xdoc/xout/sheet/@hasRetnotice>0">
				<div>
					<h3>存在有未阅读的退货通知单，请阅读退货通知单后方可再阅读订单</h3>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="/xdoc/xout/sheet">
					<xsl:call-template name="output_sheet" />
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="output_sheet">
		<div id='div_print'>
			<a href='javascript:doPrint()'>[打印本页]</a>
		</div>
		<table>
			<tr>
				<td>打印人：</td>
				<td>____________</td>
				<td>打印时间:</td>
				<td>
					<xsl:element name="div">
						<xsl:attribute name="id">divtimer</xsl:attribute>
						<xsl:attribute name="name">divtimer</xsl:attribute>
					</xsl:element>
				</td>
			</tr>
		</table>
		<table width='100%' border='0'>
			<tr>
				<td width='170'>
					<xsl:element name="img">
						<xsl:attribute name="id">print_logo</xsl:attribute>
						<xsl:attribute name="style">margin-left:15px;</xsl:attribute>
						<xsl:attribute name="src"><xsl:value-of
							select="/xdoc/xout/sheet/@logo" /></xsl:attribute>
					</xsl:element>
				</td>
				<td align='center'>
					<div id='title' style='font-size: 18px;font-weight: bold;'>
						<xsl:value-of select="/xdoc/xout/sheet/@title" />
					</div>
				</td>
				<td width='241' align='right'>
					<xsl:element name="img">
						<xsl:attribute name="id">barcode</xsl:attribute>
						<xsl:attribute name="src">../BarCode?code=<xsl:value-of
							select="/xdoc/xout/sheet/head/row/sheetid" /></xsl:attribute>
					</xsl:element>
				</td>
			</tr>
		</table>

		<xsl:if test="/xdoc/xout/sheet/head/row/flag = 7">
			<div>
				<h3 style="color:red;">注意，该订单已失效！不要送货</h3>
			</div>
		</xsl:if>

		<xsl:if test="/xdoc/xout/sheet/head/row/flag = 99">
			<div>
				<h3>注意，该订单已取消！</h3>
			</div>
		</xsl:if>

		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>

		<xsl:element name="br" />

		<xsl:for-each select="body">
			<xsl:call-template name="output_detail" />
		</xsl:for-each>

		<xsl:element name="br" />
		业务员:
		<xsl:value-of select="head/row/editor" />
		主管:
		<xsl:value-of select="head/row/checker" />
		供应商：______________________
		防损员：______________________
		收货员：______________________
		退货员：______________________
		<hr />
	</xsl:template>

	<xsl:template name="output_head">
		<xsl:element name="table">
			<xsl:attribute name="width">99%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="width">10%</xsl:attribute>
					订单编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="sheetid" />
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="width">15%</xsl:attribute>
					请按时把货送到：
				</xsl:element>
				<td colspan="3">
					<xsl:choose>
						<xsl:when test="logistics = 1">
							指定门店：[
							<xsl:value-of select="shopid" />
							]-[
							<xsl:value-of select="shopname" />
							] 地址：[
							<xsl:value-of select="shopaddr" />
							]
						</xsl:when>
						<xsl:when test="logistics = 2">
							<xsl:value-of select="destshopname" />
						</xsl:when>
						<xsl:otherwise>
							配送中心
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:element>

			<tr>
				<td>订单备注</td>
				<td colspan="5">
				</td>
			</tr>

			<xsl:element name="tr">
				<xsl:element name="td">
					课类
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="sgroupid" />
					]
					<xsl:value-of select="categoryname" />
				</xsl:element>

				<xsl:element name="td">
					结算方式
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="paytypeid" />
					]
					<xsl:value-of select="paytypename" />
				</xsl:element>

				<xsl:element name="td">
					物流模式
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="logisticsname" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					送货日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="vdeliverdate" />
				</xsl:element>

				<xsl:element name="td">
					有效期(天)
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="validdays" />
					天
				</xsl:element>

				<xsl:element name="td">
					截止日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="deadline" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					供应商
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">3</xsl:attribute>
					<xsl:value-of select="venderid" />
					<xsl:value-of select="vendername" />
				</xsl:element>
				<xsl:element name="td">
					联系电话
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="shoptel" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					预收日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="deliverdate" />
				</xsl:element>

				<xsl:element name="td">
					预收时间
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="starttime" />
					-
					<xsl:value-of select="endtime" />
				</xsl:element>

				<xsl:element name="td">
					审批单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="refsheetid" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					备注
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">5</xsl:attribute>
					<xsl:value-of select="note" />
				</xsl:element>
			</xsl:element>

		</xsl:element>

	</xsl:template>

	<xsl:template name="output_detail">
		<xsl:element name="table">
			<xsl:attribute name="width">99%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="colspan">2</xsl:attribute>
					整单订货箱数量汇总：
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">14</xsl:attribute>
					<xsl:value-of select="sum(row/pkqty)" />
					箱
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					存货地
				</xsl:element>
				<xsl:element name="th">
					商品编码
				</xsl:element>
				<xsl:element name="th">
					条形码
				</xsl:element>
				<xsl:element name="th">
					商品名称
				</xsl:element>
				<xsl:element name="th">
					规格
				</xsl:element>
				<xsl:element name="th">
					运输规格
				</xsl:element>
				<xsl:element name="th">
					订货数
				</xsl:element>
				<xsl:element name="th">
					订货箱数
				</xsl:element>
				<xsl:element name="th">
					实收数
				</xsl:element>
				<xsl:element name="th">
					进价
				</xsl:element>
				<xsl:element name="th">
					订货金额
				</xsl:element>
				<xsl:element name="th">
					折扣率
				</xsl:element>
				<xsl:element name="th">
					备注
				</xsl:element>
				<xsl:element name="th">
					件数
				</xsl:element>
				<xsl:element name="th">
					生产日期
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="row">
				<xsl:element name="tr">

					<xsl:element name="td">
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="goodsid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="barcode" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="goodsname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="spec" />
					</xsl:element>
					<xsl:element name="td">
						1箱=
						<xsl:value-of select="format-number(number(pkgvolume),'#.')" />
						<xsl:value-of select="unitname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(number(qty),'#.')" />
						<xsl:value-of select="unitname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="pkqty" />
						箱
					</xsl:element>
					<xsl:element name="td">
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(number(cost),'#0.0000')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(number(sumcost),'#0.00')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="firstdisc" />
						%
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="memo" />
					</xsl:element>
					<td>
					</td>
					<td>
					</td>
				</xsl:element>
			</xsl:for-each>
			<xsl:element name="tr">
				<xsl:element name="th">
					合计：
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="colspan">5</xsl:attribute>
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="sum(/xdoc/xout/sheet/body/row/qty)" />
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="colspan">3</xsl:attribute>
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="sum(/xdoc/xout/sheet/body/row/sumcost)" />
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="colspan">4</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>

	</xsl:template>
</xsl:stylesheet>