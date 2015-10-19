<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- main start. -->
	<xsl:template match="/">
		<xsl:call-template name="show" />
	</xsl:template>
	<xsl:template name="show">
		<xsl:element name="table">
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					城市公司
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bookname" />
				</xsl:element>
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
					单据编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/sheetid" />
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
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					结算方式
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/paytypename" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					开户银行
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">2</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bankname" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					银行帐号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">2</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bankaccno" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					电汇费
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">2</xsl:attribute>
					<xsl:value-of
						select="format-number(/xdoc/xout/head/rows/financialfee,'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					补税差
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">2</xsl:attribute>
					<xsl:value-of
						select="format-number(/xdoc/xout/head/rows/adjpayamt,'#,##0.00')" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					备注
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">5</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/note" />
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
					扣项名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					备注
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
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/chargesum_with_tax/rows/chargeamt)+sum(/xdoc/xout/chargegenerated_with_tax/rows/chargeamt),'#,##0.00')" />
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
					扣项名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					扣项金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					备注
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
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/chargesum_without_tax/rows/chargeamt)+sum(/xdoc/xout/chargegenerated_without_tax/rows/chargeamt),'#,##0.00')" />
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
					对账员：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/editor" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					日期：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/editdate" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
