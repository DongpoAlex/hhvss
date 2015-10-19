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
		<table width='100%' border='0'>
			<tr>
				<td width='170'>
					<xsl:element name="img">
						<xsl:attribute name="id">print_logo</xsl:attribute>
						<xsl:attribute name="style">margin-left:15px;</xsl:attribute>
						<xsl:attribute name="src"><xsl:value-of
							select="/xdoc/xout/sheet/@logo" />
						</xsl:attribute>
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
							select="/xdoc/xout/sheet/head/row/sheetid" />
						</xsl:attribute>
					</xsl:element>
				</td>
			</tr>
		</table>
		<xsl:for-each select="head">
			<xsl:call-template name="output_head" />

		</xsl:for-each>

		<xsl:if test="/xdoc/xout/sheet/bodydetail/row[1]/destshopid != 'A0LG'">
			<xsl:element name="br" />
			<xsl:for-each select="body">
				<xsl:call-template name="output_catalogue" />
			</xsl:for-each>
			<xsl:element name="br" />
			<xsl:call-template name="output_group" />
			<xsl:element name="br" />
			<xsl:for-each select="bodydetail">
				<xsl:call-template name="output_detail" />
			</xsl:for-each>
		</xsl:if>

		<xsl:if test="/xdoc/xout/sheet/bodydetail/row[1]/destshopid = 'A0LG'">
			<xsl:element name="br" />
			<xsl:call-template name="output_group" />
		</xsl:if>
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
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:for-each select="row">
				<xsl:element name="tr">
					<xsl:element name="th">
						审批单号
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="colspan">3</xsl:attribute>
						<xsl:value-of select="sheetid" />
					</xsl:element>
					<xsl:element name="th">
						请按时把货送到：
					</xsl:element>
					<xsl:choose>
						<xsl:when test="logistics = 1">
							<xsl:element name="td">
								指定分店
							</xsl:element>
						</xsl:when>
						<xsl:when test="logistics = 2">
							<xsl:element name="td">
								<xsl:value-of select="/xdoc/xout/sheet/bodydetail/row[1]/destshopname" />
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:element name="td">
								配送中心
							</xsl:element>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>

				<xsl:element name="tr">
					<xsl:element name="th">
						课类
					</xsl:element>
					<xsl:element name="td">
						[
						<xsl:value-of select="sgroupid" />
						]
						<xsl:value-of select="sgroupname" />
					</xsl:element>
					<xsl:element name="th">
						结算方式
					</xsl:element>
					<xsl:element name="td">
						[
						<xsl:value-of select="paytypeid" />
						]
						<xsl:value-of select="paytypename" />
					</xsl:element>
					<xsl:element name="th">
						物流模式
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="logisticsname" />
					</xsl:element>
				</xsl:element>
				<xsl:element name="tr">
					<xsl:element name="th">
						送货日期
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="orderdate" />
					</xsl:element>
					<xsl:element name="th">
						有效期(天)
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="validdays" />
						天
					</xsl:element>
					<xsl:element name="th">
						截止日期
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="deadline" />
					</xsl:element>
				</xsl:element>
				<xsl:element name="tr">
					<xsl:element name="th">
						供应商编码
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="venderid" />
					</xsl:element>
					<xsl:element name="th">
						供应商名称
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="colspan">4</xsl:attribute>
						<xsl:value-of select="vendername" />
					</xsl:element>
				</xsl:element>
				<xsl:element name="tr">
					<xsl:element name="th">
						补货标识
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="colspan">3</xsl:attribute>
						<xsl:value-of select="purchasetype" />
					</xsl:element>
					<xsl:element name="th">
						上传时间
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="colspan">2</xsl:attribute>
						<xsl:value-of select="releasedate" />
					</xsl:element>
				</xsl:element>

				<xsl:element name="tr">
					<xsl:element name="th">
						备注
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="colspan">5</xsl:attribute>
						<xsl:value-of select="note" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
		<xsl:element name="br" />
		<table width="99%">
			<div style="font-size:12; text-align: center;">
				备注：
				<xsl:element name="br" />
				郑重申明： 以上商品须提前24小时预约装货，供货商收到本采购订单后马上核对本单所有资料（送货地址、条码、价格、包装），
				<xsl:element name="br" />
				如有疑问与本采购部联系，否则，因此造成的一切损失由供货商承担
				<xsl:element name="br" />
				===========以上部分所有门店汇总，仅用于备货，具体的订货以下面的门店订单为准===========
				<xsl:element name="br" />
			</div>
		</table>
	</xsl:template>

	<xsl:template name="output_catalogue">
		<hr />
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>订单状态</th>
				<th>门店号</th>
				<th>门店名称</th>
				<th>订货通知单号</th>
				<th>预收日期</th>
				<th>预收时段</th>
				<th>备注</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="flagname" />
					</td>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="sheetid" />
					</td>
					<td>
						<xsl:value-of select="deliverdate" />
					</td>
					<td>
						<xsl:value-of select="starttime" />
						-
						<xsl:value-of select="endtime" />
					</td>
					<td>
						<xsl:value-of select="note" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template name="output_detail">
		<xsl:element name="table">
			<xsl:attribute name="width">98%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="colspan">2</xsl:attribute>
					门店、单品汇总
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="colspan">13</xsl:attribute>
					整单订货数箱量汇总：
					<xsl:value-of select="sum(/xdoc/xout/sheet/bodydetail/row/pkqty)" />
					箱
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					要货地
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
					折扣率
				</xsl:element>
				<xsl:element name="th">
					赠品数量
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
						<xsl:value-of select="format-number(number(concost),'#0.000')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="firstdisc" />
						%
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="presentqty" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:if test="memo!=''">
							<xsl:value-of select="memo" />
						</xsl:if>
						<xsl:if test="memo=''">
						</xsl:if>
					</xsl:element>
					<td>
					</td>
					<td>
					</td>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
	<xsl:template name="output_group">
		<xsl:element name="table">
			<xsl:attribute name="width">98%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="colspan">2</xsl:attribute>
					单品汇总
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="colspan">12</xsl:attribute>
					整单订货箱数量汇总：
					<xsl:value-of select="sum(/xdoc/xout/sheet/goodsgroup/row/pkqty)" />
					箱
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
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
			<xsl:for-each select="/xdoc/xout/sheet/goodsgroup/row">
				<xsl:element name="tr">
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
						<xsl:value-of select="format-number(number(cost),'#0.000')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(number(qty*cost),'#0.00')" />
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
					<xsl:attribute name="colspan">10</xsl:attribute>
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="sum(/xdoc/xout/sheet/goodsgroup/row/sumcost)" />
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="colspan">2</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
