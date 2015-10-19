<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code = 100">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					系统中目前没有发现您要的单据。
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:for-each select="/xdoc/xout/sheet">
					<xsl:call-template name="output_sheet" />
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=-1">
				<xsl:element name="div">
					<xsl:attribute name="class">warning</xsl:attribute>
					此订单没有单据明细.
				</xsl:element>
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
	<xsl:template name="output_sheet">
		<xsl:for-each select="head">
			<xsl:call-template name="output_head" />
		</xsl:for-each>
		<xsl:if
			test="/xdoc/xout/sheet/body[ @name='purchase' ]/row[1]/destshopid != 'A0LG'">
			<xsl:element name="br" />
			<xsl:for-each select="body[ @name='purchase' ]">
				<xsl:call-template name="output_catalogue" />
			</xsl:for-each>
			<xsl:element name="br" />
			<xsl:call-template name="output_group" />
			<xsl:element name="br" />
		</xsl:if>
		<xsl:if
			test="/xdoc/xout/sheet/body[ @name='purchase' ]/row[1]/destshopid = 'A0LG'">
			<xsl:element name="br" />
			<xsl:call-template name="output_group" />
		</xsl:if>
		<xsl:element name="br" />
		业务员:
		<xsl:value-of select="head/row/editor" />
		主管:
		<xsl:value-of select="head/row/checker" />
	</xsl:template>
	<xsl:template name="output_head">
		<xsl:element name="table">
			<xsl:attribute name="width">98%</xsl:attribute>
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
								<xsl:value-of
									select="/xdoc/xout/sheet/body[ @name='purchase' ]/row[1]/destshopname" />
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
						<xsl:value-of select="vdeliverdate" />
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
						<xsl:value-of select="releasetime" />
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
	</xsl:template>
	<xsl:template name="output_catalogue">
		<xsl:element name="table">
			<xsl:attribute name="width">98%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					要货单位名称
				</xsl:element>
				<xsl:element name="th">
					订单单号
				</xsl:element>
				<xsl:element name="th">
					预收日期
				</xsl:element>
				<xsl:element name="th">
					预收时段
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="row">
				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="sheetid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="deliverdate" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="delivertime" />
					</xsl:element>
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
					<xsl:attribute name="colspan">10</xsl:attribute>
					整单订货箱数量汇总：
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/goodsgroup/row/pkqty),'#.')" />
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
					送货数
				</xsl:element>
				<xsl:element name="th">
					进价
				</xsl:element>
				<xsl:element name="th">
					折扣率
				</xsl:element>
				<xsl:element name="th">
					备注
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
						<xsl:value-of
							select="format-number(number(qty) div number(pkgvolume) ,'#.')" />
						箱
					</xsl:element>
					<xsl:element name="td">
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:if test="(/xdoc/xout/sheet/head/row/paytypeflag = '购')">
							<xsl:value-of select="format-number(number(concost),'#0.000')" />
						</xsl:if>
						<xsl:if test="/xdoc/xout/sheet/head/row/paytypeflag != '购'">
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="firstdisc" />
						%
					</xsl:element>
					<xsl:element name="td">
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
