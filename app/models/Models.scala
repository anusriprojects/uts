package models

case class SiteInfo(site_id:Int,name: String)

case class SiteInfoVo(sites:List[SiteInfo],current_page:Int,total_pages:Int)
