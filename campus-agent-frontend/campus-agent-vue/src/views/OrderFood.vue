<template>
  <div class="page-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <div class="main-content">
      <div class="row justify-content-center">
        <div class="col-lg-9 col-xl-8">
          <!-- 返回按钮 -->
          <button class="back-btn" @click="$router.push('/dining')">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
              <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            返回食堂列表
          </button>

          <!-- 食堂信息头部 -->
          <div class="canteen-header">
            <div class="canteen-icon" :style="{ background: headerGradient }">
              {{ canteen.emoji }}
            </div>
            <div class="canteen-info">
              <h2 class="canteen-name">{{ canteen.name }}</h2>
              <p class="canteen-location">
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10z"/>
                  <path d="M8 8a2 2 0 1 1 0-4 2 2 0 0 1 0 4z"/>
                </svg>
                {{ canteen.location }}
              </p>
            </div>
            <div class="canteen-rating">
              <span class="rating-score">{{ canteen.rating }}</span>
              <span class="rating-label">分</span>
            </div>
          </div>

          <!-- 菜品分类标签 -->
          <div class="category-tabs">
            <button
              v-for="(cat, index) in categories"
              :key="index"
              class="category-tab"
              :class="{ active: activeCategory === index }"
              @click="activeCategory = index"
            >
              {{ cat }}
            </button>
          </div>

          <!-- 菜品列表 -->
          <div class="menu-section">
            <div class="menu-header">
              <h3>精选菜品</h3>
              <span class="menu-count">共 {{ filteredMenu.length }} 道</span>
            </div>
            <div class="menu-grid">
              <div
                v-for="(dish, index) in filteredMenu"
                :key="index"
                class="dish-card"
              >
                <div class="dish-emoji">{{ dish.emoji }}</div>
                <div class="dish-info">
                  <h4 class="dish-name">{{ dish.name }}</h4>
                  <p class="dish-desc">{{ dish.desc }}</p>
                  <div class="dish-footer">
                    <span class="dish-price">¥{{ dish.price }}</span>
                    <div class="dish-actions">
                      <button
                        v-if="getCartCount(dish.id) === 0"
                        class="add-btn"
                        @click="addToCart(dish)"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                          <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
                        </svg>
                      </button>
                      <div v-else class="quantity-control">
                        <button class="qty-btn" @click="decreaseCart(dish.id)">−</button>
                        <span class="qty-num">{{ getCartCount(dish.id) }}</span>
                        <button class="qty-btn" @click="addToCart(dish)">+</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车浮窗 -->
    <div class="cart-float" :class="{ active: cartTotal > 0 }" @click="showCartDetail = true">
      <div class="cart-icon">
        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 16 16">
          <path d="M0 1.5A.5.5 0 0 1 .5 1H2c.5 0 1 .5 1 1s-.5 1-1 1H2c.5 0 1 .5 1 1s-.5 1-1 1H2c.21 0 .45-.01.68-.06l-1.37 5.47a.5.5 0 0 1-.37.28H4.15a.5.5 0 0 1-.37-.28l-.78-2.87a.5.5 0 0 1 .02-.22L5.17 1H.5a.5.5 0 0 1-.5-.5zm3 0a.5.5 0 0 1 .5-.5h14a.5.5 0 0 1 .5.5v.5h-15v-.5zM5 14h10v1H5v-1z"/>
        </svg>
        <span class="cart-badge" v-if="cartTotal > 0">{{ cartTotal }}</span>
      </div>
      <div class="cart-info">
        <span class="cart-label">购物车</span>
        <span class="cart-amount">¥{{ cartAmount.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 购物车详情弹窗 -->
    <div class="cart-overlay" v-if="showCartDetail" @click="showCartDetail = false">
      <div class="cart-panel" @click.stop>
        <div class="cart-panel-header">
          <h3>我的购物车</h3>
          <button class="close-btn" @click="showCartDetail = false">×</button>
        </div>
        <div class="cart-panel-body">
          <div v-if="cartItems.length === 0" class="empty-cart">
            <span>购物车是空的</span>
          </div>
          <div v-else class="cart-items">
            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <div class="cart-item-left">
                <span class="cart-item-emoji">{{ item.emoji }}</span>
                <div class="cart-item-info">
                  <span class="cart-item-name">{{ item.name }}</span>
                  <span class="cart-item-price">¥{{ item.price }}</span>
                </div>
              </div>
              <div class="cart-item-right">
                <div class="quantity-control small">
                  <button class="qty-btn" @click="decreaseCart(item.id)">−</button>
                  <span class="qty-num">{{ item.count }}</span>
                  <button class="qty-btn" @click="addToCart(item)">+</button>
                </div>
                <span class="cart-item-subtotal">¥{{ (item.price * item.count).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="cart-panel-footer">
          <div class="cart-summary">
            <span class="summary-label">共 {{ cartTotal }} 份</span>
            <span class="summary-amount">合计 ¥{{ cartAmount.toFixed(2) }}</span>
          </div>
          <button class="checkout-btn" @click="handleCheckout" :disabled="cartItems.length === 0">
            去结算
          </button>
        </div>
      </div>
    </div>

    <!-- 结算成功提示 -->
    <div class="toast" :class="{ show: showToast }">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
        <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
      </svg>
      {{ toastMessage }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderFood',
  data() {
    return {
      canteen: {},
      canteenIndex: 0,
      activeCategory: 0,
      categories: ['全部', '热销', '主食', '小菜', '饮品'],
      menuData: {
        0: [ // 第一食堂
          { id: 1, emoji: '🍜', name: '红烧牛肉面', desc: '精选牛肉，软糯入味', price: 15.00, category: '主食' },
          { id: 2, emoji: '🍝', name: '番茄鸡蛋面', desc: '酸甜可口，营养丰富', price: 12.00, category: '主食' },
          { id: 3, emoji: '🥟', name: '鲜肉水饺', desc: '手工现包，皮薄馅大', price: 10.00, category: '主食' },
          { id: 4, emoji: '🥚', name: '茶叶蛋', desc: '香气四溢，早餐必备', price: 1.50, category: '小菜' },
          { id: 5, emoji: '🥛', name: '现磨豆浆', desc: '香浓醇厚，活力满满', price: 3.00, category: '饮品' },
          { id: 6, emoji: '🥖', name: '油条', desc: '外酥里嫩，金黄酥脆', price: 2.00, category: '小菜' },
          { id: 7, emoji: '🔥', name: '热销套餐', desc: '主食+小菜+饮品', price: 18.00, category: '热销' },
        ],
        1: [ // 第二食堂
          { id: 101, emoji: '🍱', name: '糖醋排骨套餐', desc: '酸甜开胃，分量十足', price: 16.00, category: '主食' },
          { id: 102, emoji: '🥘', name: '鱼香肉丝', desc: '经典川味，下饭神器', price: 14.00, category: '主食' },
          { id: 103, emoji: '🍆', name: '红烧茄子', desc: '软糯入味，经济实惠', price: 8.00, category: '小菜' },
          { id: 104, emoji: '🥬', name: '蒜蓉青菜', desc: '清爽健康，每日必点', price: 6.00, category: '小菜' },
          { id: 105, emoji: '🥤', name: '柠檬水', desc: '清凉解渴，夏日首选', price: 4.00, category: '饮品' },
          { id: 106, emoji: '🍚', name: '米饭', desc: '东北大米，香软可口', price: 1.00, category: '主食' },
          { id: 107, emoji: '⭐', name: '今日特惠', desc: '限时优惠，超值享受', price: 12.00, category: '热销' },
        ],
        2: [ // 第三食堂
          { id: 201, emoji: '🌶️', name: '麻辣香锅', desc: '香辣过瘾，食材丰富', price: 22.00, category: '主食' },
          { id: 202, emoji: '🍲', name: '麻辣烫', desc: '自选搭配，暖心暖胃', price: 18.00, category: '主食' },
          { id: 203, emoji: '🥩', name: '火锅套餐', desc: '二人世界，情侣首选', price: 58.00, category: '热销' },
          { id: 204, emoji: '🍢', name: '烤串拼盘', desc: '夜宵必备，撸串必备', price: 15.00, category: '小菜' },
          { id: 205, emoji: '🥤', name: '冰镇酸梅汤', desc: '解辣神器，冰凉解渴', price: 5.00, category: '饮品' },
          { id: 206, emoji: '🥘', name: '干锅花菜', desc: '香辣可口，下饭神器', price: 16.00, category: '主食' },
          { id: 207, emoji: '🔥', name: '招牌推荐', desc: '本店爆款，不容错过', price: 28.00, category: '热销' },
        ],
        3: [ // 清真食堂
          { id: 301, emoji: '🥙', name: '羊肉泡馍', desc: '正宗西北风味', price: 20.00, category: '主食' },
          { id: 302, emoji: '🍖', name: '红烧牛腩', desc: '软烂入味，香气扑鼻', price: 25.00, category: '主食' },
          { id: 303, emoji: '🍢', name: '羊肉串', desc: '新鲜羊肉，炭火烤制', price: 3.00, category: '小菜' },
          { id: 304, emoji: '🥘', name: '大盘鸡', desc: '一份管饱，聚餐首选', price: 48.00, category: '热销' },
          { id: 305, emoji: '🍵', name: '砖茶', desc: '消食解腻，回族传统', price: 2.00, category: '饮品' },
          { id: 306, emoji: '🫓', name: '烤馕', desc: '外脆内软，麦香四溢', price: 3.00, category: '小菜' },
          { id: 307, emoji: '⭐', name: '必点推荐', desc: '进店首选，口碑保证', price: 18.00, category: '热销' },
        ]
      },
      cart: {},
      showCartDetail: false,
      showToast: false,
      toastMessage: '',
      headerGradient: 'linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%)'
    }
  },
  computed: {
    canteenList() {
      return [
        { name: '第一食堂', emoji: '🍜', location: '主教学楼东侧', rating: '4.8' },
        { name: '第二食堂', emoji: '🍛', location: '图书馆对面', rating: '4.6' },
        { name: '第三食堂', emoji: '🍲', location: '体育馆旁', rating: '4.7' },
        { name: '清真食堂', emoji: '🥙', location: '东门附近', rating: '4.5' }
      ]
    },
    filteredMenu() {
      const menu = this.menuData[this.canteenIndex] || []
      if (this.activeCategory === 0) return menu
      const cat = this.categories[this.activeCategory]
      return menu.filter(item => item.category === cat)
    },
    cartItems() {
      return Object.values(this.cart).filter(item => item.count > 0)
    },
    cartTotal() {
      return this.cartItems.reduce((sum, item) => sum + item.count, 0)
    },
    cartAmount() {
      return this.cartItems.reduce((sum, item) => sum + item.price * item.count, 0)
    }
  },
  created() {
    const index = parseInt(this.$route.params.id) || 0
    this.canteenIndex = index
    this.canteen = this.canteenList[index] || this.canteenList[0]

    const gradients = [
      'linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%)',
      'linear-gradient(135deg, #f9ca24 0%, #f0932b 100%)',
      'linear-gradient(135deg, #eb4d4b 0%, #b33939 100%)',
      'linear-gradient(135deg, #6ab04c 0%, #38a169 100%)'
    ]
    this.headerGradient = gradients[index % gradients.length]
  },
  methods: {
    getCartCount(dishId) {
      return this.cart[dishId]?.count || 0
    },
    addToCart(dish) {
      const cart = { ...this.cart }
      if (!cart[dish.id]) {
        cart[dish.id] = { ...dish, count: 0 }
      }
      cart[dish.id].count++
      this.cart = cart
    },
    decreaseCart(dishId) {
      if (this.cart[dishId]) {
        const cart = { ...this.cart }
        cart[dishId].count--
        if (cart[dishId].count <= 0) {
          delete cart[dishId]
        }
        this.cart = cart
      }
    },
    handleCheckout() {
      if (this.cartItems.length === 0) return
      this.showCartDetail = false
      this.toastMessage = `订单提交成功！共 ${this.cartTotal} 份，合计 ¥${this.cartAmount.toFixed(2)}`
      this.showToast = true
      this.cart = {}
      setTimeout(() => {
        this.showToast = false
      }, 3000)
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 50%, #f0f2f5 100%);
  padding-bottom: 80px;
}

.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s ease-in-out infinite;
}

.circle-1 {
  width: 350px;
  height: 350px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  top: -80px;
  right: -80px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #f9ca24 0%, #f0932b 100%);
  bottom: -60px;
  left: -60px;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -20px) scale(1.05); }
}

.main-content {
  position: relative;
  z-index: 1;
  padding: 24px 20px 30px;
}

/* 返回按钮 */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 7px 14px;
  background: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: #667eea;
  cursor: pointer;
  transition: all 0.25s ease;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
  letter-spacing: 0.3px;
}

.back-btn:hover {
  transform: translateX(-3px) scale(1.03);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

/* 食堂头部 */
.canteen-header {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.canteen-icon {
  width: 60px;
  height: 60px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.canteen-info {
  flex: 1;
}

.canteen-name {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 6px;
}

.canteen-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.canteen-rating {
  text-align: center;
}

.rating-score {
  font-size: 28px;
  font-weight: 700;
  color: #ff6b6b;
  display: block;
}

.rating-label {
  font-size: 12px;
  color: #6b7280;
}

/* 分类标签 */
.category-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.category-tab {
  padding: 8px 16px;
  background: white;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.category-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.category-tab:hover:not(.active) {
  background: #f0f4ff;
  color: #667eea;
}

/* 菜品区域 */
.menu-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.menu-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.menu-count {
  font-size: 12px;
  color: #9ca3af;
}

.menu-grid {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.dish-card {
  display: flex;
  gap: 14px;
  padding: 14px;
  background: #fafbfc;
  border-radius: 12px;
  transition: all 0.25s ease;
}

.dish-card:hover {
  background: #f0f4ff;
}

.dish-emoji {
  width: 50px;
  height: 50px;
  background: white;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.dish-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dish-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 4px;
}

.dish-desc {
  font-size: 12px;
  color: #9ca3af;
  margin: 0 0 8px;
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dish-price {
  font-size: 18px;
  font-weight: 700;
  color: #ff6b6b;
}

.dish-actions {
  display: flex;
  align-items: center;
}

.add-btn {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.add-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.4);
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-control.small {
  gap: 6px;
}

.qty-btn {
  width: 26px;
  height: 26px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.qty-btn:hover {
  transform: scale(1.1);
}

.qty-num {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
  min-width: 20px;
  text-align: center;
}

/* 购物车浮窗 */
.cart-float {
  position: fixed;
  bottom: 24px;
  right: 24px;
  background: #9ca3af;
  border-radius: 28px;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.cart-float.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.cart-float:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
}

.cart-icon {
  position: relative;
  color: white;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -10px;
  background: #ff6b6b;
  color: white;
  font-size: 11px;
  font-weight: 700;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.cart-info {
  display: flex;
  flex-direction: column;
}

.cart-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.8);
}

.cart-amount {
  font-size: 15px;
  font-weight: 700;
  color: white;
}

/* 购物车面板 - 居中模态框 */
.cart-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 200;
  display: flex;
  align-items: center;  /* 垂直居中 */
  justify-content: center;  /* 水平居中 */
  padding: 20px;
}

.cart-panel {
  width: 100%;
  max-width: 480px;  /* 限制最大宽度 */
  max-height: 80vh;
  background: white;
  border-radius: 20px;  /* 全圆角（不是只有顶部） */
  display: flex;
  flex-direction: column;
  animation: fadeInUp 0.3s ease;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.cart-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.cart-panel-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.close-btn {
  width: 28px;
  height: 28px;
  background: #f5f5f5;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
  color: #9ca3af;
  font-size: 14px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-item-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-item-emoji {
  font-size: 24px;
}

.cart-item-info {
  display: flex;
  flex-direction: column;
}

.cart-item-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
}

.cart-item-price {
  font-size: 12px;
  color: #9ca3af;
}

.cart-item-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.cart-item-subtotal {
  font-size: 14px;
  font-weight: 600;
  color: #ff6b6b;
}

.cart-panel-footer {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafbfc;
  border-radius: 0 0 20px 20px;  /* 底部左右圆角 */
}

.cart-summary {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.summary-label {
  font-size: 13px;
  color: #6b7280;
}

.summary-amount {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}

.checkout-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.25s ease;
}

.checkout-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Toast 提示 */
.toast {
  position: fixed;
  top: 50%;
  left: calc(50% + 120px);
  transform: translate(-50%, -50%) scale(0.8);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 16px 28px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 10px;
  opacity: 0;
  transition: all 0.3s ease;
  z-index: 300;
}

.toast.show {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1);
}

.toast svg {
  color: #22c55e;
}

@media (max-width: 768px) {
  .main-content {
    padding: 16px 14px 100px;
  }

  .canteen-header {
    padding: 16px;
  }

  .canteen-icon {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }

  .canteen-name {
    font-size: 18px;
  }

  .rating-score {
    font-size: 22px;
  }

  .cart-float {
    bottom: 16px;
    right: 16px;
    padding: 10px 16px;
  }

  .cart-overlay {
    left: 0;
  }

  .toast {
    left: 50%;
  }
}
</style>
