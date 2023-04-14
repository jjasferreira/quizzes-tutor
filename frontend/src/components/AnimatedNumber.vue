<template>
  <span>{{ displayNumber }}<slot /></span>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';

@Component
export default class AnimatedNumber extends Vue {
  @Prop({ default: 0 }) readonly number!: number;
  displayNumber: number = 0;
  interval: number = 0;

  created() {
    this.updateNumber();
  }

  @Watch('number')
  updateNumber() {
    clearInterval(0);
    if (this.number == this.displayNumber) {
      return;
    }
    this.interval = window.setInterval(() => {
      if (this.displayNumber < this.number) {
        if (this.number % 1 == 0) {
          this.displayNumber = Math.round(this.number);
        }
        else {
          this.displayNumber = parseFloat(this.number.toFixed(2));
        }

      }
    }, 20);
  }
}
</script>

<style scoped lang="scss" />
