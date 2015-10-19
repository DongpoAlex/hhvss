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
		<div id='div_print'>
			<a href='javascript:doPrint()'>[打印本页]</a>
		</div>
		<table width='100%' border='0'>
			<tr>
				<td width='170'>
					<xsl:element name="img">
						<xsl:attribute name="id">print_logo</xsl:attribute>
						<xsl:attribute name="style">margin-left:15px;</xsl:attribute>
						<xsl:attribute name="src">../img/crv_logo.jpg</xsl:attribute>
					</xsl:element>
				</td>
				<td align='center'>
					<div id='title' style='font-size: 18px;font-weight: bold;'>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/printtitle" />
					</div>
				</td>
				<td width='241' align='right'>
				</td>
			</tr>
		</table>

		<!-- 促销员协议 -->
		<xsl:if test="/xdoc/xout/sheet/head/row/printstyle = 0">
			<xsl:for-each select="head/row">
				<xsl:call-template name="output_head0" />
			</xsl:for-each>
			<xsl:for-each select="body">
				<xsl:call-template name="output_body0" />
			</xsl:for-each>
			<xsl:call-template name="charge0" />
			<table width="99%" cellspacing="0" cellpadding="4" border="1">
				<tr>
					<td width="50%" height="50">甲方（盖章）：</td>
					<td width="50%" height="50">乙方（盖章）：</td>
				</tr>
				<tr>
					<td>委托代理人（品类负责人）： </td>
					<td>委托代理人（品类负责人）：</td>
				</tr>
				<tr>
					<td>＿＿ 年＿＿ 月＿＿ 日 </td>
					<td>＿＿ 年＿＿ 月＿＿ 日</td>
				</tr>
			</table>
		</xsl:if>
		<!-- 促销陈列协议 -->
		<xsl:if test="/xdoc/xout/sheet/head/row/printstyle = 1">
			<xsl:for-each select="head/row">
				<xsl:call-template name="output_head1" />
			</xsl:for-each>
			<xsl:for-each select="body">
				<xsl:call-template name="output_body1" />
			</xsl:for-each>
			<xsl:call-template name="charge1" />
			<table width="99%" cellspacing="0" cellpadding="4" border="1">
				<tr>
					<td width="50%" height="50">甲方（盖章）：</td>
					<td width="50%" height="50">乙方（盖章）：</td>
				</tr>
				<tr>
					<td>委托代理人（品类负责人）： </td>
					<td>委托代理人（品类负责人）：</td>
				</tr>
				<tr>
					<td>＿＿ 年＿＿ 月＿＿ 日 </td>
					<td>＿＿ 年＿＿ 月＿＿ 日</td>
				</tr>
			</table>
		</xsl:if>
		<!-- 其它协议 -->
		<xsl:if test="/xdoc/xout/sheet/head/row/printstyle = 2">
			<xsl:for-each select="head/row">
				<xsl:call-template name="output_head2" />
			</xsl:for-each>
			<xsl:for-each select="body">
				<xsl:call-template name="output_body2" />
			</xsl:for-each>
			<xsl:call-template name="charge2" />
		</xsl:if>
		<xsl:if test="/xdoc/xout/sheet/head/row/printstyle = 3">
			<xsl:call-template name="charge3" />
		</xsl:if>



	</xsl:template>


	<xsl:template name="output_head0">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>单据号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td>单据状态</td>
				<td>
					<xsl:value-of select="flagname" />
				</td>
			</tr>
			<tr>
				<td>业态</td>
				<td>
					<xsl:value-of select="shopformname" />
				</td>
				<td>采购区域</td>
				<td>
					<xsl:value-of select="regionname" />
				</td>
			</tr>
			<tr>
				<td>合作部门</td>
				<td colspan="3">
					<xsl:value-of select="departname" />
				</td>
			</tr>
			<tr>
				<td>合作课类</td>
				<td>
					<xsl:value-of select="majorid" />
				</td>
				<td>课类名称</td>
				<td>
					<xsl:value-of select="categoryname" />
				</td>
			</tr>
			<tr>
				<td>供应商编号</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td>供应商名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
			<tr>
				<td>扣项名称</td>
				<td colspan="3">
					<xsl:value-of select="chargename" />
				</td>
			</tr>
			<tr>
				<td>生效日期</td>
				<td>
					<xsl:value-of select="checkdate" />
				</td>
				<td>计划收费日期</td>
				<td>
					<xsl:value-of select="sdate" />
				</td>
			</tr>
			<tr>
				<td>付款方式</td>
				<td colspan="3">
					<xsl:value-of select="settlemodename" />
				</td>
			</tr>
			<tr>
				<td>供应商联系人</td>
				<td>
					<xsl:value-of select="vendercon" />
				</td>
				<td>供应商电话号码</td>
				<td>
					<xsl:value-of select="vendertel" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_head1">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>单据号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td>单据状态</td>
				<td>
					<xsl:value-of select="flagname" />
				</td>
			</tr>
			<tr>
				<td>业态</td>
				<td>
					<xsl:value-of select="shopformname" />
				</td>
				<td>采购区域</td>
				<td>
					<xsl:value-of select="regionname" />
				</td>
			</tr>
			<tr>
				<td>合作部门</td>
				<td colspan="3">
					<xsl:value-of select="departname" />
				</td>
			</tr>
			<tr>
				<td>合作课类</td>
				<td>
					<xsl:value-of select="majorid" />
				</td>
				<td>课类名称</td>
				<td>
					<xsl:value-of select="categoryname" />
				</td>
			</tr>
			<tr>
				<td>供应商编号</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td>供应商名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
			<tr>
				<td>扣项名称</td>
				<td colspan="3">
					<xsl:value-of select="chargename" />
				</td>
			</tr>
			<tr>
				<td>生效日期</td>
				<td>
					<xsl:value-of select="checkdate" />
				</td>
				<td>计划收费日期</td>
				<td>
					<xsl:value-of select="sdate" />
				</td>
			</tr>
			<tr>
				<td>付款方式</td>
				<td colspan="3">
					<xsl:value-of select="settlemodename" />
				</td>
			</tr>
			<tr>
				<td>供应商联系人</td>
				<td>
					<xsl:value-of select="vendercon" />
				</td>
				<td>供应商电话号码</td>
				<td>
					<xsl:value-of select="vendertel" />
				</td>
			</tr>
			<tr>
				<td>备注</td>
				<td colspan="3">
					<xsl:value-of select="note" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_head2">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>
					单据号：
					<xsl:value-of select="sheetid" />
				</td>
				<td>
					状态：
					<xsl:value-of select="flagname" />
				</td>
				<td colspan="2">
					备注：
					<xsl:value-of select="note" />
				</td>
			</tr>
			<tr>
				<td>
					制单人：
					<xsl:value-of select="editor" />
				</td>
				<td>
					制单日期：
					<xsl:value-of select="editdate" />
				</td>
				<td>
					审核人：
					<xsl:value-of select="checker" />
				</td>
				<td>
					审核日期：
					<xsl:value-of select="checkdate" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_body0">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>门店编码</td>
				<td>门店名称</td>
				<td>促销员人数</td>
				<td>促销员类型</td>
				<td>入职类型</td>
				<td>管理费(续签免收)</td>
				<td>培训费(续签免收)</td>
				<td>合计</td>
				<td>促销开始日期</td>
				<td>促销结束日期</td>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="promqty" />
					</td>
					<td>
						<xsl:value-of select="promtypename" />
					</td>
					<td>
						<xsl:value-of select="onboardtypename" />
					</td>
					<td>
						<xsl:value-of select="format-number(managecharge,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(traincharge,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(chargevalue,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="begindate" />
					</td>
					<td>
						<xsl:value-of select="enddate" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>合计大写金额</td>
				<td colspan="6">
					人民币：
					<xsl:value-of select="/xdoc/xout/sheet/chinese" />
				</td>
				<td>合计小写金额</td>
				<td colspan="2">
					<xsl:value-of select="format-number(sum(row/chargevalue),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td>品类采购签名</td>
				<td colspan="2">
				</td>
				<td>品类负责人签名</td>
				<td colspan="2">
				</td>
				<td>品类总监签名</td>
				<td colspan="3">
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_body1">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>门店编码</td>
				<td>门店名称</td>
				<td>数量</td>
				<td>应付金额</td>
				<td>陈列方式</td>
				<td>促销开始日期</td>
				<td>促销结束日期</td>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="promqty" />
					</td>
					<td>
						<xsl:value-of select="format-number(chargevalue,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="shelfid" />
					</td>
					<td>
						<xsl:value-of select="begindate" />
					</td>
					<td>
						<xsl:value-of select="enddate" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>合计大写金额</td>
				<td colspan="4">
					人民币：
					<xsl:value-of select="/xdoc/xout/sheet/chinese" />
				</td>
				<td>合计小写金额</td>
				<td>
					<xsl:value-of select="format-number(sum(row/chargevalue),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td>品类采购签名</td>
				<td>
				</td>
				<td>品类负责人签名</td>
				<td>
				</td>
				<td>品类总监签名</td>
				<td colspan="2">
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_body2">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>门店名称</td>
				<td>业态名称</td>
				<td>供应商编码</td>
				<td>供应商名称</td>
				<td>课类编码</td>
				<td>课类名称</td>
				<td>区域标识</td>
				<td>采购员</td>
				<td>扣项代码</td>
				<td>扣项名称</td>
				<td>费用金额</td>
				<td>帐扣标志</td>
				<td>是否盖章</td>
				<td>备注</td>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/shopformname" />
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/venderid" />
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/majorid" />
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/categoryname" />
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/regionname" />
					</td>
					<td>
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/chargecodeid" />
					</td>
					<td>
						<xsl:value-of select="/xdoc/xout/sheet/head/row/chargename" />
					</td>
					<td>
						<xsl:value-of select="format-number(chargevalue,'#,##0.00')" />
					</td>
					<td>
					</td>
					<td>
					</td>
					<td>
						<xsl:value-of select="shelfid" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="3">合计金额（大写）：人民币</td>
				<td colspan="5">
					<xsl:value-of select="/xdoc/xout/sheet/chinese" />
				</td>
				<td colspan="6">
					合计金额：￥
					<xsl:value-of select="format-number(sum(row/chargevalue),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td>采购：</td>
				<td colspan="2">
				</td>
				<td>品类负责人：</td>
				<td colspan="2">
				</td>
				<td>财务盖章（收现或银行转账）：</td>
				<td colspan="3">
				</td>
				<td colspan="4">供应商签字/盖章：</td>
			</tr>
			<tr>
				<td colspan="10">
				</td>
				<td colspan="4">供应商承诺:贵司已经履行相应义务,我司同意按上述方式支付表中所列各项费用。</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="charge0">
		<div style="font-size:16px;padding:4px;">甲方：商业有限公司</div>
		<div style="font-size:16px;padding:4px;">
			乙方：
			<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
		</div>
		<div>
			根据中华人民共和国相关法律，甲、乙双方在自愿、平等的基础上，经友好协商，就乙方委托甲方对其派驻促销员进行培训管理并在甲方门店开展推广活动达成如下协议：
			<br></br>
			一、培训管理具体内容及费用收取（见协议内容）
			<br></br>
			二、双方权利义务
			<br></br>
			1、乙方派驻甲方的促销人员应接受乙方完整、专业的培训，熟悉乙方的商品和有关售后服务的规定，进驻甲方店内时必须持有效协议正本及所需相关材料，经甲方门店促销部检验合格后上岗。
			<br></br>
			2、乙方保证与所派驻促销员签订合法合同并购买法定保险，承担促销员工资、奖金及其它相关费用。
			<br></br>
			3、为保护促销员合法权益，在此乙方同意，如乙方拖欠促销员工资，甲方有权暂停乙方账款结算并代为发放拖欠工资，所需款项从乙方账款中扣除。
			<br></br>
			4、乙方派驻人员在店内的工作，要服从店内的统一领导和工作安排，并严格遵守甲方的促销人员相关管理规定。
			<br></br>
			5、乙方派驻人员在店内期间，商业对员工的各项纪律和安全条例对派驻人员具有同等效力。
			<br></br>
			6、如因乙方责任含乙方促销员责任（包括但不限于能力胜任岗位要求、诚信问题等）发生促销员离职，乙方必须在三日内保证新促销员替换到位，否则，视为乙方违约。
			<br></br>
			7、如乙方促销员在门店的考勤、工作纪律等方面出现违规行为，乙方应督促其改进，否则，视为乙方违约，乙方应承担给甲方造成的损失。
			<br></br>
			8、以上各项乙方如有违反，甲方有权终止此项协议。
			<br></br>
			9、供应商凭“商业促销员协议”或“系统审核单据号”，到门店办理相关手续。
			<br></br>

			三、附则
			<br></br>
			1、  本协议履行过程中，若发生纠纷，双方应协商解决，如果协商未果，任何一方均可向甲方所在地人民法院提出诉讼。
			<br></br>
			2、  经乙方加盖章后生效。
			<br></br>
		</div>
	</xsl:template>

	<xsl:template name="charge1">
		<div style="font-size:16px;padding:4px;">甲方：商业有限公司</div>
		<div style="font-size:16px;padding:4px;">
			乙方：
			<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
		</div>
		<div>
			根据中华人民共和国相关法律，甲、乙双方在自愿、平等的基础上，经友好协商，就乙方商品委托甲方门店特殊方式促销，乙方承担所有促销费用，达成如下协议：
			<br></br>
			一、特殊陈列内容及费用收取（见协议内容）：
			<br></br>
			二、双方权利义务
			<br></br>
			1、 
			乙方如选择“扣货款”形式缴纳相关费用，则必须保证在甲方帐户余额足够，否则，甲方具有追诉权；乙方如选择“现金支票”，“银行转帐”形式缴纳费用，则必须实际款项到达甲方帐户后，方可要求甲方给予促销陈列安排。
			<br></br>
			2、  甲方负责审核乙方陈列与整体形象是否符合甲方品牌形象和各门店具体要求。
			<br></br>
			3、  供应商凭分店（供应商）联到门店办理相关手续。
			<br></br>
			4、  乙方保证所销售产品不存在任何权利瑕疵，否则，愿承担由于某种由于侵权所造成的甲方一切损失。
			<br></br>
			三、附则
			<br></br>
			1、  本协议履行过程中，若发生纠纷，双方应协商解决，如果协商未果，任何一方均可向甲方所在地人民法院提出诉讼。
			<br></br>
			2、  经乙方加盖章后生效。
			<br></br>
		</div>
	</xsl:template>

	<xsl:template name="charge2">
	</xsl:template>

	<xsl:template name="charge3">
		<div style="font-size:16px;padding:4px;">甲方：商业有限公司</div>
		<div style="font-size:16px;padding:4px;">
			乙方：
			<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
		</div>
		<div>
			根据中华人民共和国相关法律，甲、乙双方在自愿、平等的基础上，经友好协商，就乙方商品委托甲方门店特殊方式促销，乙方承担所有促销费用，达成如下协议：
			<br></br>
			一、特列陈列内容及费用收取（见收费通知单）：
			<br></br>
			二、双方权利义务
			<br></br>
			1、 
			乙方如选择“扣货款”形式缴纳相关费用，则必须保证在甲方帐户余额足够，否则，甲方具有追诉权；乙方如选择“现金支票”，“很行转帐”形式缴纳费用，则必须实际款项到达甲方帐户后，方可要求甲方给予促销陈列安排。
			<br></br>
			2、  甲方负责审核乙方陈列与整体形象是否符合甲方品牌形象和各门店具体要求。
			<br></br>
			3、  供应商凭分店（供应商）联到门店办理相关手续。
			<br></br>
			4、  乙方保证所销售产品不存在任何权利瑕疵，否则，愿承担由于某种由于侵权所造成的甲方一切损失。
			<br></br>
			三、附则
			<br></br>
			1、  本协议履行过程中，若发生纠纷，双方应协商解决，如果协商未果，任何一方均可向甲方所在地人民法院提出诉讼。
			<br></br>
			2、  本协议一式二份：财务联，供应商（分店）联。经乙方加盖章，甲方加盖采购部促销专用章后生效。
			<br></br>
		</div>
	</xsl:template>
</xsl:stylesheet>