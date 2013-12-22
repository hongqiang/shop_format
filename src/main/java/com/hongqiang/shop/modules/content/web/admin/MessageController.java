package com.hongqiang.shop.modules.content.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hongqiang.shop.common.utils.Pageable;
import com.hongqiang.shop.common.web.BaseController;
import com.hongqiang.shop.modules.content.service.MessageService;
import com.hongqiang.shop.modules.entity.Member;
import com.hongqiang.shop.modules.user.service.MemberService;
import com.hongqiang.shop.common.utils.Message;

@Controller("adminMessageController")
@RequestMapping({ "${adminPath}/message" })
public class MessageController extends BaseController {

	@Autowired
	MessageService messageService;

	@Autowired
	MemberService memberService;

	@RequestMapping(value = { "/check_username" }, method = RequestMethod.GET)
	@ResponseBody
	public boolean checkUsername(String username) {
		return this.memberService.usernameExists(username);
	}

	@RequestMapping(value = { "/send" }, method = RequestMethod.GET)
	public String send(Long draftMessageId, Model model) {
		com.hongqiang.shop.modules.entity.Message localMessage = (com.hongqiang.shop.modules.entity.Message) this.messageService
				.find(draftMessageId);
		if ((localMessage != null)
				&& (localMessage.getIsDraft().booleanValue())
				&& (localMessage.getSender() == null))
			model.addAttribute("draftMessage", localMessage);
		return "admin/message/send";
	}

	@RequestMapping(value = { "/send" }, method = RequestMethod.POST)
	public String send(Long draftMessageId, String username, String title,
			String content,
			@RequestParam(defaultValue = "false") Boolean isDraft,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (!beanValidator(com.hongqiang.shop.modules.entity.Message.class,
				"content", content, new Class[0]))
			return ERROR_PAGE;
		com.hongqiang.shop.modules.entity.Message localMessage1 = (com.hongqiang.shop.modules.entity.Message) this.messageService
				.find(draftMessageId);
		if ((localMessage1 != null)
				&& (localMessage1.getIsDraft().booleanValue())
				&& (localMessage1.getSender() == null))
			this.messageService.delete(localMessage1);
		Member localMember = null;
		if (StringUtils.isNotEmpty(username)) {
			localMember = this.memberService.findByUsername(username);
			if (localMember == null)
				return ERROR_PAGE;
		}
		com.hongqiang.shop.modules.entity.Message localMessage2 = new com.hongqiang.shop.modules.entity.Message();
		localMessage2.setTitle(title);
		localMessage2.setContent(content);
		localMessage2.setIp(request.getRemoteAddr());
		localMessage2.setIsDraft(isDraft);
		localMessage2.setSenderRead(Boolean.valueOf(true));
		localMessage2.setReceiverRead(Boolean.valueOf(false));
		localMessage2.setSenderDelete(Boolean.valueOf(false));
		localMessage2.setReceiverDelete(Boolean.valueOf(false));
		localMessage2.setSender(null);
		localMessage2.setReceiver(localMember);
		localMessage2.setForMessage(null);
		localMessage2.setReplyMessages(null);
		this.messageService.save(localMessage2);
		if (isDraft.booleanValue()) {
			addMessage(redirectAttributes, Message.success(
					"admin.message.saveDraftSuccess", new Object[0]));
			return "redirect:draft.jhtml";
		}
		addMessage(redirectAttributes,
				Message.success("admin.message.sendSuccess", new Object[0]));
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/view" }, method = RequestMethod.GET)
	public String view(Long id, Model model) {
		com.hongqiang.shop.modules.entity.Message localMessage = 
				(com.hongqiang.shop.modules.entity.Message) this.messageService
				.find(id);
		if ((localMessage == null)
				|| (localMessage.getIsDraft().booleanValue())
				|| (localMessage.getForMessage() != null))
			return ERROR_PAGE;
		if (((localMessage.getSender() != null) && (localMessage.getReceiver() != null))
				|| ((localMessage.getReceiver() == null) && (localMessage
						.getReceiverDelete().booleanValue()))
				|| ((localMessage.getSender() == null) && (localMessage
						.getSenderDelete().booleanValue())))
			return ERROR_PAGE;
		if (localMessage.getReceiver() == null)
			localMessage.setReceiverRead(Boolean.valueOf(true));
		else
			localMessage.setSenderRead(Boolean.valueOf(true));
		this.messageService.update(localMessage);
		model.addAttribute("adminMessage", localMessage);
		return "/admin/message/view";
	}

	@RequestMapping(value = { "/reply" }, method = RequestMethod.POST)
	public String reply(Long id, String content, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(com.hongqiang.shop.modules.entity.Message.class,
				"content", content, new Class[0]))
			return ERROR_PAGE;
		com.hongqiang.shop.modules.entity.Message localMessage1 = (com.hongqiang.shop.modules.entity.Message) this.messageService
				.find(id);
		if ((localMessage1 == null)
				|| (localMessage1.getIsDraft().booleanValue())
				|| (localMessage1.getForMessage() != null))
			return ERROR_PAGE;
		if (((localMessage1.getSender() != null) && (localMessage1
				.getReceiver() != null))
				|| ((localMessage1.getReceiver() == null) && (localMessage1
						.getReceiverDelete().booleanValue()))
				|| ((localMessage1.getSender() == null) && (localMessage1
						.getSenderDelete().booleanValue())))
			return ERROR_PAGE;
		com.hongqiang.shop.modules.entity.Message localMessage2 = new com.hongqiang.shop.modules.entity.Message();
		localMessage2.setTitle("reply: " + localMessage1.getTitle());
		localMessage2.setContent(content);
		localMessage2.setIp(request.getRemoteAddr());
		localMessage2.setIsDraft(Boolean.valueOf(false));
		localMessage2.setSenderRead(Boolean.valueOf(true));
		localMessage2.setReceiverRead(Boolean.valueOf(false));
		localMessage2.setSenderDelete(Boolean.valueOf(false));
		localMessage2.setReceiverDelete(Boolean.valueOf(false));
		localMessage2.setSender(null);
		localMessage2
				.setReceiver(localMessage1.getReceiver() == null ? localMessage1
						.getSender() : localMessage1.getReceiver());
		if (((localMessage1.getReceiver() == null) && (!localMessage1
				.getSenderDelete().booleanValue()))
				|| ((localMessage1.getSender() == null) && (!localMessage1
						.getReceiverDelete().booleanValue())))
			localMessage2.setForMessage(localMessage1);
		localMessage2.setReplyMessages(null);
		this.messageService.save(localMessage2);
		if (localMessage1.getSender() == null) {
			localMessage1.setSenderRead(Boolean.valueOf(true));
			localMessage1.setReceiverRead(Boolean.valueOf(false));
		} else {
			localMessage1.setSenderRead(Boolean.valueOf(false));
			localMessage1.setReceiverRead(Boolean.valueOf(true));
		}
		this.messageService.update(localMessage1);
		if (((localMessage1.getReceiver() == null) && (!localMessage1
				.getSenderDelete().booleanValue()))
				|| ((localMessage1.getSender() == null) && (!localMessage1
						.getReceiverDelete().booleanValue()))) {
			addMessage(redirectAttributes, ADMIN_SUCCESS);
			return "redirect:view.jhtml?id=" + localMessage1.getId();
		}
		addMessage(redirectAttributes,
				Message.success("admin.message.replySuccess", new Object[0]));
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	public String list(Pageable pageable, Model model) {
		model.addAttribute("page", this.messageService.findPage(null, pageable));
		return "/admin/message/list";
	}

	@RequestMapping(value = { "/draft" }, method = RequestMethod.GET)
	public String draft(Pageable pageable, Model model) {
		model.addAttribute("page",
				this.messageService.findDraftPage(null, pageable));
		return "/admin/message/draft";
	}

	@RequestMapping(value = { "delete" }, method = RequestMethod.POST)
	@ResponseBody
	public Message delete(Long[] ids) {
		if (ids != null)
			for (Long localLong : ids)
				this.messageService.delete(localLong, null);
		return ADMIN_SUCCESS;
	}
}