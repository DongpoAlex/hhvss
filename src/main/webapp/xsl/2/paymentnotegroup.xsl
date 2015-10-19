<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- main start. -->
	<xsl:template match="/">
		<xsl:call-template name="show_sheetlist" />
	</xsl:template>
	<xsl:template name="show_sheetlist">
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					单据编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/sheetid" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					城市公司
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bookname" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					供应商编码
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/venderid" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					供应商名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/vendername" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					开户银行
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bankname" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					银行帐号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bankaccno" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					支付方式
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/paymodename" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本期对帐金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/payamt)-number(/xdoc/xout/head/rows/chargeamt),'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					计划付款日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/planpaydate" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					17%税建议开票金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/invtotalamt17),'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					13%税建议开票金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/invtotalamt13),'#,##0.00')" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					0%税建议开票金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/invtotalamt0),'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					预付款冲抵金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/suspayamt),'#,##0.00')" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					扣项一合计金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/chargesum_with_tax/rows/chargeamt)+sum(/xdoc/xout/chargegenerated_with_tax/rows/chargeamt),'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					扣项二合计金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/chargesum_without_tax/rows/chargeamt)+sum(/xdoc/xout/chargegenerated_without_tax/rows/chargeamt),'#,##0.00')" />

				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本期实付金额（大写）
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/real_pay" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本期实付金额（小写）
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/payamt)-number(/xdoc/xout/head/rows/adjpayamt)-number(/xdoc/xout/head/rows/financialfee)-number(/xdoc/xout/head/rows/suspayamt),'#,##0.00')" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					供应商盖章
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">3</xsl:attribute>
					（盖章处）
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">10</xsl:attribute>
					对帐单据
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					单据类型
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店编码
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					对帐金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					物流模式
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					单据发生日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					结算方式
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					课类
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					单据说明
				</xsl:element>
			</xsl:element>

			<xsl:for-each select="/xdoc/xout/sheetset/unpaidsheet_list/rows">
				<xsl:sort select="docno" order="descending" data-type="text" />
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="docno" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="doctypename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopname" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(number(total_payamt),'#,##0.00')" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="logisticsid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="docdate" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="paytypename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="majorid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="noteremark" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>


			<xsl:for-each select="/xdoc/xout/sheetset/salecost_list/rows">
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="doctypename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopname" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(total_payamt,'#,##0.00')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="logisticsid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="docdate" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="paytypename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="majorid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>


			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">

					<xsl:attribute name="align">center</xsl:attribute>
					合计:
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">4</xsl:attribute>
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheetset/unpaidsheet_list/rows/total_payamt)+sum(/xdoc/xout/sheetset/salecost_list/rows/total_payamt),'#,##0.00')" />
					<xsl:element name="td">
						<xsl:attribute name="colspan">5</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">7</xsl:attribute>
					扣项一(票扣明细)
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项说明
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/chargesum_with_tax/rows">
				<xsl:sort select="docno" order="descending" data-type="text" />
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="docno" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="chargename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:if test="chargeamt!=''">
							<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
						</xsl:if>
						<xsl:if test="chargeamt=''">
							<xsl:value-of select="chargeamt" />
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="noteremark" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					合计:
				</xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:if test="/xdoc/xout/chargesum_with_tax/rows/chargeamt!=''">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/chargesum_with_tax/rows/chargeamt),'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/chargesum_with_tax/rows/chargeamt=''">
						<xsl:value-of select="/xdoc/xout/chargesum_with_tax/rows/chargeamt" />
					</xsl:if>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">2</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">7</xsl:attribute>
					固定扣项(票扣)
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项说明
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/chargegenerated_with_tax/rows">
				<xsl:sort select="docno" order="descending" data-type="text" />
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="docno" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="chargename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:if test="chargeamt!=''">
							<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
						</xsl:if>
						<xsl:if test="chargeamt=''">
							<xsl:value-of select="chargeamt" />
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="noteremark" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					合计:
				</xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:if test="/xdoc/xout/chargegenerated_with_tax/rows/chargeamt!=''">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/chargegenerated_with_tax/rows/chargeamt),'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/chargegenerated_with_tax/rows/chargeamt=''">
						<xsl:value-of select="/xdoc/xout/chargegenerated_with_tax/rows/chargeamt" />
					</xsl:if>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">2</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">7</xsl:attribute>
					扣项二(非票扣明细)
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项说明
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/chargesum_without_tax/rows">
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="docno" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="chargename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:if test="chargeamt!=''">
							<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
						</xsl:if>
						<xsl:if test="chargeamt=''">
							<xsl:value-of select="chargeamt" />
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="noteremark" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					合计:
				</xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:if test="/xdoc/xout/chargesum_without_tax/rows/chargeamt!=''">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/chargesum_without_tax/rows/chargeamt),'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/chargesum_without_tax/rows/chargeamt=''">
						<xsl:value-of select="/xdoc/xout/chargesum_without_tax/rows/chargeamt" />
					</xsl:if>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">2</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">7</xsl:attribute>
					固定扣项(非票扣)
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					门店名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项说明
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/chargegenerated_without_tax/rows">
				<xsl:sort select="docno" order="descending" data-type="text" />
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="docno" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="chargename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:if test="chargeamt!=''">
							<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
						</xsl:if>
						<xsl:if test="chargeamt=''">
							<xsl:value-of select="chargeamt" />
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="noteremark" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					合计:
				</xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td"></xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:if test="/xdoc/xout/chargegenerated_without_tax/rows/chargeamt!=''">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/chargegenerated_without_tax/rows/chargeamt),'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/chargegenerated_without_tax/rows/chargeamt=''">
						<xsl:value-of
							select="/xdoc/xout/chargegenerated_without_tax/rows/chargeamt" />
					</xsl:if>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">2</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本期实付金额(大写)
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/real_pay" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本期实付金额(小写)
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/payamt)-number(/xdoc/xout/head/rows/adjpayamt)-number(/xdoc/xout/head/rows/financialfee)-number(/xdoc/xout/head/rows/suspayamt),'#,##0.00')" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					供应商盖章
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">3</xsl:attribute>
					（盖章处）
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					备注
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">3</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/note" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					注意
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">3</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					1、打印供应商对账明细单必须设置页码，每页对账单必须加盖公章或财务专用章。
					<br />
					2、如果供应商在计划付款日期之后或者之前的7天内交票，我公司付款日期将按交票日+7天逢周五付款进行处理。
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					制单人：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/editor" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					制单日期：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/editdate" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
