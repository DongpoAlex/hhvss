<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code = 100">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					系统中目前没有发现您要的单据。
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:for-each select="/xdoc/xout/report">
					<xsl:call-template name="show_sheetlist" />
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

	<xsl:template name="show_sheetlist">
		注意：此结算限额只对代销和联营供应商生效，购销供应商仅做参考，购销结算仍按05年的方法进行。
		<br />
		<xsl:element name="table">
			<xsl:attribute name="style">FONT-SIZE: 12px; BORDER-COLLAPSE: collapse</xsl:attribute>
			<xsl:attribute name="border">0</xsl:attribute>
			<xsl:attribute name="width">90%</xsl:attribute>
			<xsl:attribute name="cellspadding">5</xsl:attribute>
			<xsl:attribute name="cellSpacing">1</xsl:attribute>

			<xsl:element name="COLGROUP">
				<xsl:element name="COL">
					<xsl:attribute name="ruwidthles">10%</xsl:attribute>
					<xsl:attribute name="ruwidthles">10%</xsl:attribute>
					<xsl:attribute name="ruwidthles">10%</xsl:attribute>
					<xsl:attribute name="ruwidthles">60%</xsl:attribute>
					<xsl:attribute name="ruwidthles">10%</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="colspan">1</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					供应商编码:
					<xsl:value-of select="row/venderid" />
				</xsl:element>
				<xsl:attribute name="align">left</xsl:attribute>
				<xsl:element name="th">
					<xsl:attribute name="colspan">3</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					供应商名称:
					<xsl:value-of select="row/vendername" />
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="align">right</xsl:attribute>
					单位:元
				</xsl:element>


			</xsl:element>

		</xsl:element>
		<xsl:element name="table">

			<xsl:attribute name="style">FONT-SIZE: 12px; BORDER-COLLAPSE: collapse</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:attribute name="width">90%</xsl:attribute>
			<xsl:attribute name="cellspadding">3</xsl:attribute>
			<xsl:attribute name="cellSpacing">1</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					月份
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					所属分公司
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					上月结余
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					本月增加
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					本月付款
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					本月结余
				</xsl:element>
			</xsl:element>

			<xsl:for-each select="row">
				<xsl:sort select="accmonth" order="ascending" data-type="text" />
				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="accmonth" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="bookname" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of
							select="format-number( (openadviceamt+opentaxamt),'#,##0.00')" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of
							select="format-number( (incadviceamt+inctaxamt),'#,##0.00')" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of
							select="format-number( (decadviceamt+dectaxamt),'#,##0.00')" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of select="format-number( (adviceamt+taxamt),'#,##0.00')" />
					</xsl:element>

				</xsl:element>
			</xsl:for-each>

		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
