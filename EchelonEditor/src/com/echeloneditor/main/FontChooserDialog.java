package com.echeloneditor.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/**
 * 字体设置页面
 * 
 * @author yangzm
 * 
 */
@SuppressWarnings("rawtypes")
public class FontChooserDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel panFont = null;
	private JLabel labFontName = null;
	private JTextField txtFontName = null;
	private JScrollPane spanFontName = null;
	private JList listFontName = null;
	private JLabel labFontModel = null;
	private JTextField txtFontModel = null;
	private JList listFontModel = null;
	private JLabel labFontSize = null;
	private JTextField txtFontSize = null;
	private JScrollPane spanFontSize = null;
	private JList listFontSize = null;
	private JPanel panShow = null;
	private JLabel labShow = null;
	private JComponent compoment = null;

	/**
	 * 字形信息
	 */
	private String[] fontModelName = { "常规", "粗体", "斜体" };

	/**
	 * 字形对应值
	 */
	private int[] fontModelValue = { Font.PLAIN, Font.BOLD, Font.ITALIC };

	/**
	 * 字体大小
	 */
	private String[] fontSize = { "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "42", "46", "48" };
	private JScrollPane spanFontModel = null;
	private JButton btOK = null;
	private JButton btCancel = null;

	/**
	 * This method initializes panFont
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanFont() {
		if (panFont == null) {
			labFontSize = new JLabel();
			labFontSize.setBounds(new Rectangle(269, 2, 68, 19));
			labFontSize.setText("大小：");
			labFontModel = new JLabel();
			labFontModel.setBounds(new Rectangle(165, 2, 59, 19));
			labFontModel.setText("字形：");
			labFontName = new JLabel();
			labFontName.setBounds(new Rectangle(3, 2, 59, 19));
			labFontName.setText("字体：");
			panFont = new JPanel();
			panFont.setLayout(null);
			panFont.setBounds(new Rectangle(5, 5, 351, 155));
			panFont.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			panFont.add(labFontName, null);
			panFont.add(getTxtFontName(), null);
			panFont.add(getSpanFontName(), null);
			panFont.add(labFontModel, null);
			panFont.add(getTxtFontModel(), null);
			panFont.add(labFontSize, null);
			panFont.add(getTxtFontSize(), null);
			panFont.add(getSpanFontSize(), null);
			panFont.add(getSpanFontModel(), null);
		}
		return panFont;
	}

	/**
	 * This method initializes txtFontName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtFontName() {
		if (txtFontName == null) {
			txtFontName = new JTextField();
			txtFontName.setBounds(new Rectangle(3, 23, 152, 19));
			txtFontName.setText("宋体");
		}
		return txtFontName;
	}

	/**
	 * This method initializes spanFontName
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getSpanFontName() {
		if (spanFontName == null) {
			spanFontName = new JScrollPane();
			spanFontName.setBounds(new Rectangle(3, 44, 152, 104));
			spanFontName.setViewportView(getListFontName());
		}
		return spanFontName;
	}

	/**
	 * This method initializes listFontName
	 * 
	 * @return javax.swing.JList
	 */
	@SuppressWarnings("unchecked")
	private JList getListFontName() {
		if (listFontName == null) {

			listFontName = new JList();

			// 获取本机安装的字体
			GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] fontNames = gEnv.getAvailableFontFamilyNames();
			DefaultListModel model = new DefaultListModel();
			for (String fontName : fontNames) {
				model.addElement(fontName);
			}

			listFontName.setModel(model);

			listFontName.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					getTxtFontName().setText(getListFontName().getSelectedValue().toString());
					updateShow();
					if (null != compoment) {
						compoment.setFont(fetchFont());
					}
				}
			});
		}
		return listFontName;
	}

	/**
	 * This method initializes txtFontModel
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtFontModel() {
		if (txtFontModel == null) {
			txtFontModel = new JTextField();
			txtFontModel.setBounds(new Rectangle(165, 23, 96, 19));
			txtFontModel.setText("常规");
		}
		return txtFontModel;
	}

	/**
	 * This method initializes listFontModel
	 * 
	 * @return javax.swing.JList
	 */
	@SuppressWarnings("unchecked")
	private JList getListFontModel() {
		if (listFontModel == null) {
			listFontModel = new JList();

			DefaultListModel model = new DefaultListModel();
			for (String modelName : fontModelName) {
				model.addElement(modelName);
			}
			listFontModel.setModel(model);

			listFontModel.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					getTxtFontModel().setText(getListFontModel().getSelectedValue().toString());
					updateShow();
					if (null != compoment) {
						compoment.setFont(fetchFont());
					}
				}
			});
		}
		return listFontModel;
	}

	/**
	 * This method initializes txtFontSize
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtFontSize() {
		if (txtFontSize == null) {
			txtFontSize = new JTextField();
			txtFontSize.setBounds(new Rectangle(269, 23, 77, 19));
			txtFontSize.setText("8");
		}
		return txtFontSize;
	}

	/**
	 * This method initializes spanFontSize
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getSpanFontSize() {
		if (spanFontSize == null) {
			spanFontSize = new JScrollPane();
			spanFontSize.setBounds(new Rectangle(269, 44, 77, 104));
			spanFontSize.setViewportView(getListFontSize());
		}
		return spanFontSize;
	}

	/**
	 * This method initializes listFontSize
	 * 
	 * @return javax.swing.JList
	 */
	@SuppressWarnings("unchecked")
	private JList getListFontSize() {
		if (listFontSize == null) {
			listFontSize = new JList();

			DefaultListModel model = new DefaultListModel();
			for (String size : fontSize) {
				model.addElement(size);
			}
			listFontSize.setModel(model);
			listFontSize.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					getTxtFontSize().setText(getListFontSize().getSelectedValue().toString());
					updateShow();
					if (null != compoment) {
						compoment.setFont(fetchFont());
					}
				}
			});
		}
		return listFontSize;
	}

	/**
	 * This method initializes panShow
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanShow() {
		if (panShow == null) {
			labShow = new JLabel();
			labShow.setBounds(new Rectangle(31, 18, 302, 51));
			labShow.setHorizontalTextPosition(SwingConstants.CENTER);
			labShow.setHorizontalAlignment(SwingConstants.CENTER);
			labShow.setText("中文字体 AaBbYy");
			panShow = new JPanel();
			panShow.setLayout(null);
			panShow.setBounds(new Rectangle(5, 164, 351, 78));
			panShow.setBorder(BorderFactory.createTitledBorder(null, "示例", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("宋体", Font.PLAIN, 12), new Color(153, 84, 10)));
			panShow.add(labShow, null);
			labShow.setBorder(BorderFactory.createBevelBorder(1));
		}
		return panShow;
	}

	/**
	 * @param owner
	 * @wbp.parser.constructor
	 */
	public FontChooserDialog(Frame owner, Font oldFont, JComponent compoment) {
		super(owner);
		initialize();

		this.compoment = compoment;
		setResizable(false);
		makeFont(oldFont);
	}

	public FontChooserDialog(Frame owner, Font oldFont) {
		super(owner);
		initialize();

		makeFont(oldFont);
		setResizable(false);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(445, 279);
		this.setResizable(false);
		this.setTitle("字体选择");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getPanFont(), null);
			jContentPane.add(getPanShow(), null);
			jContentPane.add(getBtOK(), null);
			jContentPane.add(getBtCancel(), null);
		}
		return jContentPane;
	}

	/**
	 * 设置字体
	 */
	private void makeFont(Font font) {
		if (null != font) {
			getTxtFontName().setText(font.getFontName());
			getListFontName().setSelectedValue(font.getFontName(), true);
			getListFontName().ensureIndexIsVisible(getListFontName().getSelectedIndex());

			String fontModel = fontModelName[0];
			if (font.isPlain()) {
				fontModel = getFontModelNameByValue(Font.PLAIN);
			}
			if (font.isBold()) {
				fontModel = getFontModelNameByValue(Font.BOLD);
			}
			if (font.isItalic()) {
				fontModel = getFontModelNameByValue(Font.ITALIC);
			}
			getTxtFontModel().setText(fontModel);
			getListFontModel().setSelectedValue(fontModel, true);
			getListFontModel().ensureIndexIsVisible(getListFontName().getSelectedIndex());

			getTxtFontSize().setText(String.valueOf(font.getSize()));
			getListFontSize().setSelectedValue(String.valueOf(font.getSize()), true);
			getListFontSize().ensureIndexIsVisible(getListFontName().getSelectedIndex());

			labShow.setFont(font);
		}
	}

	/**
	 * 根据字体模型的Value获取fontModelName的值
	 */
	private String getFontModelNameByValue(int value) {
		String name = fontModelName[0];

		for (int i = 0; i < fontModelValue.length; i++) {
			int _value = fontModelValue[i];
			if (value == _value) {
				name = fontModelName[i];
				break;
			}
		}

		return name;
	}

	/**
	 * 获取新设置的字体
	 */
	private Font fetchFont() {

		String fontName = getTxtFontName().getText().trim();
		String fontModel = getTxtFontModel().getText().trim();
		int FontSize = Integer.parseInt(getTxtFontSize().getText());
		Font font = new Font(fontName, getFontModelValueByName(fontModel), FontSize);
		return font;
	}

	/**
	 * 根据fontModel的名称获取fontModelValue的值
	 */
	private int getFontModelValueByName(String modelName) {
		int result = Font.PLAIN;

		for (int i = 0; i < fontModelName.length; i++) {
			String _modelName = fontModelName[i];
			if (modelName.equals(_modelName)) {
				result = fontModelValue[i];
				break;
			}
		}

		return result;
	}

	/**
	 * 更新示例的字体
	 */
	private void updateShow() {
		labShow.setFont(fetchFont());
	}

	/**
	 * This method initializes spanFontModel
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getSpanFontModel() {
		if (spanFontModel == null) {
			spanFontModel = new JScrollPane();
			spanFontModel.setBounds(new Rectangle(165, 44, 96, 104));
			spanFontModel.setViewportView(getListFontModel());
		}
		return spanFontModel;
	}

	/**
	 * This method initializes btOK
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtOK() {
		if (btOK == null) {
			btOK = new JButton();
			btOK.setBounds(new Rectangle(365, 14, 65, 24));
			btOK.setText("确定");
			btOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					okOption(e);
				}
			});
		}
		return btOK;
	}

	/**
	 * 确定操作-设置传进来的控件的字体
	 */
	private void okOption(ActionEvent e) {
		if (null != compoment) {
			compoment.setFont(fetchFont());
			dispose();
		}
	}

	/**
	 * This method initializes btCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBtCancel() {
		if (btCancel == null) {
			btCancel = new JButton();
			btCancel.setBounds(new Rectangle(365, 46, 65, 24));
			btCancel.setText("取消");
			btCancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return btCancel;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		final JFrame frame = new JFrame("Font Test");
		frame.setSize(400, 300);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		JButton btShow = new JButton("设置字体");
		btShow.setLocation(10, 10);
		btShow.setSize(120, 23);

		final JTextArea txt = new JTextArea("简体中文 AaBbCc");
		txt.setLocation(10, 40);
		txt.setSize(280, 130);
		txt.setBorder(BorderFactory.createLineBorder(Color.gray));
		frame.getContentPane().add(txt);

		btShow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = txt.getFont();

				FontChooserDialog fontset = new FontChooserDialog(frame, font, txt);
				fontset.setLocationRelativeTo(frame);
				fontset.setVisible(true);
			}
		});

		frame.getContentPane().add(btShow);

		frame.setVisible(true);

	}

}
